package com.hsrm.umweltrechner.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import com.hsrm.umweltrechner.syntax.Interpreter;
import com.hsrm.umweltrechner.syntax.SymbolTable;
import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormulaInterpreterService {

  @Qualifier("FormelInterpreter")
  private final Interpreter interpreter;

  private final SensorMapper sensorMapper;

  private final FormulaMapper formulaMapper;

  private final VariableMapper variablesMapper;

  private final CustomerAlertsService customerAlertsService;

  private final ConcurrentHashMap<String, SymbolTable.SymbolEntry> lastUpdates =
      new ConcurrentHashMap<>();


  @Autowired
  public FormulaInterpreterService(
      Interpreter formulaInterpreter,
      SensorMapper sensorMapper,
      FormulaMapper formulaMapper,
      VariableMapper variablesMapper,
      CustomerAlertsService customerAlertsService) {
    this.interpreter = formulaInterpreter;
    this.sensorMapper = sensorMapper;
    this.formulaMapper = formulaMapper;
    this.variablesMapper = variablesMapper;
    this.customerAlertsService = customerAlertsService;
  }

  @PostConstruct
  @Transactional
  public void init() {
    sensorMapper.selectAll().forEach(sensor -> {
      try {
        interpreter.addSensor(sensor.getName(), 0.0);
      } catch (OutOfRangeException | InvalidSymbolException e) {
        throw new RuntimeException(e);
      }
    });
    List<Formula> formulas = formulaMapper.selectAll();
    String eqs = formulas.stream().map(Formula::getFormula).collect(Collectors.joining("\n"));
    try {
      interpreter.checkSyntax(eqs);
      interpreter.setEquations(eqs);
      interpreter.calculate();
    } catch (Exception e) {
      log.error("Error while parsing formula " + e);
    }

    List<String> variables = interpreter.getVariables().keySet().stream().toList();
    List<Variable> variablesFromTable = variablesMapper.selectAll();
    List<String> nameOfVariablesInTable = new ArrayList<>();

    for (var variableFromTable : variablesFromTable) {
      nameOfVariablesInTable.add(variableFromTable.getName());
    }
    for (var variable : variables) {
      if (!nameOfVariablesInTable.contains(variable)) {
        variablesMapper.insert(
            Variable.builder().name(variable).maxThreshold(null).minThreshold(null).build());
      }
    }
    for (var variable : nameOfVariablesInTable) {
      if (!variables.contains(variable)) {
        variablesMapper.deleteByName(variable);
      }
    }
  }

  public void addSensorValue(String sensorName, Double value, long ts) throws OutOfRangeException,
      InvalidSymbolException {
    interpreter.addSensor(sensorName, value, ts);
  }

  public void registerSensor(String sensorName, Double value) throws OutOfRangeException,
      InvalidSymbolException {
    interpreter.addSensor(sensorName, value);
  }

  public List<DtoVariableData> calculateAndGetVariables() {
    if (interpreter.getVariables().isEmpty()) {
      return new LinkedList<>();
    }
    try {
      interpreter.calculate();
      customerAlertsService.processThresholds(interpreter.getVariables());
    } catch (Exception e) {
      log.error("Error while calculating formula", e);
    }

    // only return variables/got a new timestamp that have changed from last computation
    HashMap<String, SymbolTable.SymbolEntry> xs = interpreter.getVariablesWithFlag();
    List<DtoVariableData> result = new LinkedList<>();
    xs.forEach((key, value) -> {
      long lastModified = value.getLastModified();
      SymbolTable.SymbolEntry lastUpdate = lastUpdates.get(key);
      if (lastUpdate == null || lastUpdate.getLastModified() != lastModified) {
        lastUpdates.put(key, value);
        result.add(new DtoVariableData(key, value.getValue(), lastModified));
      }
    });
    return result;
  }



  public void checkSyntax(String formula) throws DivideByZeroException, DomainException,
      UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    interpreter.checkSyntax(formula);
  }

  public boolean variableExists(String variableName) {
    return interpreter.symbolExists(variableName);
  }


}
