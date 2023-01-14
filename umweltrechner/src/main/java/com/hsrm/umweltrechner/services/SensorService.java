package com.hsrm.umweltrechner.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.Sensor;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SensorService {

  private final SensorMapper sensorMapper;

  private final FormulaInterpreterService formulaInterpreterService;

  private final VariableMapper variableMapper;


  @Autowired
  public SensorService(SensorMapper sensorMapper,
      FormulaInterpreterService formulaInterpreterService, VariableMapper variableMapper) {
    this.sensorMapper = sensorMapper;
    this.formulaInterpreterService = formulaInterpreterService;
    this.variableMapper = variableMapper;
  }

  public Sensor addOrUpdateSensor(Sensor sensor) throws OutOfRangeException,
      InvalidSymbolException {
    sensorMapper.deleteByName(sensor.getName());
    sensorMapper.insert(sensor);
    formulaInterpreterService.registerSensor(sensor.getName(), sensor.getValue() == null ?
        0 : sensor.getValue());
    variableMapper.insert(
        Variable.builder().name(sensor.getName()).maxThreshold(null).minThreshold(null).build());
    return sensor;
  }

  public List<Sensor> getAllSensors() {
    return sensorMapper.selectAll();
  }
}
