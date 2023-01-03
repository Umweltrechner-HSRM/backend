package com.hsrm.umweltrechner.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.syntax.FormelInterpreter;
import com.hsrm.umweltrechner.syntax.exception.DivideByZeroException;
import com.hsrm.umweltrechner.syntax.exception.DomainException;
import com.hsrm.umweltrechner.syntax.exception.IllegalWriteException;
import com.hsrm.umweltrechner.syntax.exception.IncorrectSyntaxException;
import com.hsrm.umweltrechner.syntax.exception.InvalidSymbolException;
import com.hsrm.umweltrechner.syntax.exception.OutOfRangeException;
import com.hsrm.umweltrechner.syntax.exception.UnknownSymbolException;
import com.hsrm.umweltrechner.syntax.Interpreter;
import com.hsrm.umweltrechner.syntax.SymbolTable;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormulaInterpreterService {

  @Qualifier("FormelInterpreter")
  private final Interpreter interpreter;

  private final VariableService variableService;

  private final SensorMapper sensorMapper;

  private final FormulaMapper formulaMapper;

  private final VariableMapper variablesMapper;


  @Autowired
  public FormulaInterpreterService(
      Interpreter formulaInterpreter,
      VariableService variableService, SensorMapper sensorMapper,
      FormulaMapper formulaMapper,
      VariableMapper variablesMapper) {
    this.interpreter = formulaInterpreter;
    this.variableService = variableService;
    this.sensorMapper = sensorMapper;
    this.formulaMapper = formulaMapper;
    this.variablesMapper = variablesMapper;
  }

  @PostConstruct
  private void init() {
    sensorMapper.selectAll().forEach(sensor -> {
      double value = sensor.getValue() != null ? sensor.getValue() : (double) 10.0;
      try {
        interpreter.addSensor(sensor.getName(), value);
      } catch (OutOfRangeException | InvalidSymbolException e) {
        throw new RuntimeException(e);
      }
    });
    formulaMapper.selectAll().forEach(formula -> {
      try {
        interpreter.checkSyntax(formula.getFormula());
        interpreter.setEquations(formula.getFormula());
        interpreter.calculate();
      } catch (Exception e) {
        log.error("Error while parsing formula " + formula.getFormula(), e);
      }
    });

    List<String> variables = getVariableNames();
    List<Variable> variablesFromTable = variablesMapper.selectAll();
    List<String> nameOfVariablesInTable = new ArrayList<>();

    for (var variableFromTable : variablesFromTable){
      nameOfVariablesInTable.add(variableFromTable.getName());
    }
    for (var variable : variables){
      if (!nameOfVariablesInTable.contains(variable)){
        variablesMapper.insert(
            Variable.builder().name(variable).maxThreshold(null).minThreshold(null).build());
      }
    }
    for (var variable : nameOfVariablesInTable){
      if (!variables.contains(variable)){
        variablesMapper.deleteByName(variable);
      }
    }
  }

  public void addSensorValue(String sensorName, Double value, Long ts) throws OutOfRangeException, InvalidSymbolException {
    if (value == null) {
      value = (double) 0xBabeCafe;
    }
    long timestamp = ts != null ? ts : System.currentTimeMillis();
    interpreter.addSensor(sensorName, value, timestamp);
  }

  public void addFormula() {
    init();
  }


  public HashMap<String, SymbolTable.SymbolEntry> calculateAndGetVariables() {
    if (interpreter.getVariables().isEmpty()) {
      return null;
    }
    try {
      interpreter.calculate();
      List<Variable> variableList = variableService.getAllVariables();
      for (var variable: variableList){
        boolean isDanger = false;
        if (variable.getMinThreshold() != null && interpreter.getVariables().get(variable.getName()) <= variable.getMinThreshold()) {
          isDanger = true;
        }
        if (variable.getMaxThreshold() != null && interpreter.getVariables().get(variable.getName()) >= variable.getMaxThreshold()) {
          isDanger = true;
        }
        if(isDanger){
          log.warn("Variable " + variable.getName() + " is in danger!");
        }
      }
      // compare thresholds from variable table with calculated values
      // and send signal
    } catch (Exception e) {
      log.error("Error while calculating formula", e);
    }
    return interpreter.getVariablesWithFlag();
  }

  public List<String> getVariableNames() {
    return interpreter.getVariables().keySet().stream().toList();
  }


  public void checkSyntax(String formula) throws DivideByZeroException, DomainException, UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    interpreter.checkSyntax(formula);
  }

  public boolean variableExists(String variableName) {
    return interpreter.symbolExists(variableName);
  }


}
