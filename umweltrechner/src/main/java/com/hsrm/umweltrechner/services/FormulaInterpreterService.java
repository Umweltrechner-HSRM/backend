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
import com.hsrm.umweltrechner.dao.model.types.VariableType;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;
import com.hsrm.umweltrechner.syntax.Interpreter;
import com.hsrm.umweltrechner.syntax.SymbolTable;

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
  public void syncFormulaSystem() {
    interpreter.clearVariables();
    interpreter.clearSymbolTable();
    sensorMapper.selectAll().forEach(sensor -> {
      try {
        interpreter.addSensor(sensor.getName(), 0.0);
      } catch (OutOfRangeException | InvalidSymbolException e) {
        throw new RuntimeException(e);
      }
    });
    List<Formula> formulas = formulaMapper.selectAll();


    for (int i = 0; i < formulas.size(); i++) {
      String slice =
          formulas.stream().limit(i + 1).map(Formula::getFormula).collect(Collectors.joining("\n"));
      try {
        interpreter.checkSyntax(slice);
        interpreter.setEquations(slice);
        interpreter.calculate();
      } catch (Exception e) {
        formulaMapper.delete(formulas.get(i).getId());
        log.error("Formula {} is invalid and will be deleted", formulas.get(i).getId());
      }
    }

    HashMap<String, SymbolTable.SymbolEntry> variables = interpreter.getVariablesWithFlag();
    List<Variable> variablesFromTable = variablesMapper.selectAll();
    List<String> nameOfVariablesInTable = new ArrayList<>();

    for (Variable variableFromTable : variablesFromTable) {
      nameOfVariablesInTable.add(variableFromTable.getName());
    }
    for (String variable : variables.keySet()) {
      if (!nameOfVariablesInTable.contains(variable)) {
        variablesMapper.insert(
            Variable.builder()
                .name(variable)
                .maxThreshold(null)
                .minThreshold(null)
                .type(variables.get(variable).isReadOnly() ? VariableType.SENSOR : VariableType.FORMULA)
                .build());
      }
    }
    for (var variable : nameOfVariablesInTable) {
      if (!variables.containsKey(variable)) {
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
