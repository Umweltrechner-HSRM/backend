package com.hsrm.umweltrechner.services;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
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

  private final SensorMapper sensorMapper;

  private final FormulaMapper formulaMapper;


  @Autowired
  public FormulaInterpreterService(
      Interpreter formulaInterpreter,
      SensorMapper sensorMapper,
      FormulaMapper formulaMapper) {
    this.interpreter = formulaInterpreter;
    this.sensorMapper = sensorMapper;
    this.formulaMapper = formulaMapper;
  }

  @PostConstruct
  private void init() {
    sensorMapper.selectAll().forEach(sensor -> {
      double value = sensor.getValue() != null ? sensor.getValue() : (double) 0xBabeCafe;
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
