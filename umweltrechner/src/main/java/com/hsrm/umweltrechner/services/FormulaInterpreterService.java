package com.hsrm.umweltrechner.services;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
import com.hsrm.umweltrechner.syntax.FormelInterpreter;
import com.hsrm.umweltrechner.syntax.Interpreter;

@Service
@EnableScheduling
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
  public void init() {
    sensorMapper.selectAll().forEach(sensor -> {
      double value = sensor.getValue() != null ? sensor.getValue() : 0;
      interpreter.addSensor(sensor.getName(), value);
    });
    formulaMapper.selectAll().forEach(formula -> {
      try {
        interpreter.checkSyntax(formula.getFormula());
        interpreter.setEquations(formula.getFormula());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public void addSensorValue(String sensorName, double value) {
    if (interpreter.getVariables().containsKey(sensorName)) {
      interpreter.addSensor(sensorName, value);
    }
  }



  public HashMap<String, FormelInterpreter.SymbolEntry> getVariables() {
    try {
      interpreter.calculate();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return interpreter.getVariablesWithFlag();
  }


}
