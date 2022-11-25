package com.hsrm.umweltrechner.services;

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
import com.hsrm.umweltrechner.syntax.Interpreter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormulaInterpreterService {

  @Qualifier("FormelInterpreter")
  private final Interpreter interpreter;

  private final SensorMapper sensorMapper;

  private final FormulaMapper formulaMapper;

  private final VariableMapper variablesMapper;


  @Autowired
  public FormulaInterpreterService(
      Interpreter formulaInterpreter,
      SensorMapper sensorMapper,
      FormulaMapper formulaMapper,
      VariableMapper variablesMapper) {
    this.interpreter = formulaInterpreter;
    this.sensorMapper = sensorMapper;
    this.formulaMapper = formulaMapper;
    this.variablesMapper = variablesMapper;
  }

  @PostConstruct
  private void init() {
    sensorMapper.selectAll().forEach(sensor -> {
      double value = sensor.getValue() != null ? sensor.getValue() : (double) 0xBabeCafe;
      interpreter.addSensor(sensor.getName(), value);
    });
    formulaMapper.selectAll().forEach(formula -> {
      try {
        interpreter.checkSyntax(formula.getFormula());
        interpreter.setEquations(formula.getFormula());
        interpreter.calculate();

        // update variables table
        // if variable is in interpreter.variables() but not in table, insert it
        // if variable is in table but not in interpreter.variables(), delete it? (threshold will
        // be removed from table)
        List<String> variables = getVariableNames();
        List<Variable> variableFromTable = variablesMapper.selectAll();






      } catch (Exception e) {
        log.error("Error while parsing formula " + formula.getFormula(), e);
      }
    });
  }

  public void addSensorValue(String sensorName, Double value) {
    if (value == null) {
      value = (double) 0xBabeCafe;
    }
    interpreter.addSensor(sensorName, value);
  }

  public void addFormula(){
    init();
  }


  public HashMap<String, FormelInterpreter.SymbolEntry> calculateAndGetVariables() {
    if(interpreter.getVariables().isEmpty()){
      return null;
    }
    try {
      interpreter.calculate();
      // compare thresholds from variable table with calculated values
      // and send signal
    } catch (Exception e) {
      log.error("Error while calculating formula", e);
    }
    return interpreter.getVariablesWithFlag();
  }

  public List<String> getVariableNames(){
    return interpreter.getVariables().keySet().stream().toList();
  }


  public void checkSyntax(String formula) throws FormelInterpreter.IllegalWriteException,
      FormelInterpreter.UnknownVariableException, FormelInterpreter.IncorrectSyntaxException {
    interpreter.checkSyntax(formula);
  }


}
