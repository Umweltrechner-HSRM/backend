package com.hsrm.umweltrechner.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.SensorMapper;
import com.hsrm.umweltrechner.dao.model.Sensor;
import com.hsrm.umweltrechner.syntax.exception.InvalidSymbolException;
import com.hsrm.umweltrechner.syntax.exception.OutOfRangeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SensorService {

  private final SensorMapper sensorMapper;

  private final FormulaInterpreterService formulaInterpreterService;


  @Autowired
  public SensorService(SensorMapper sensorMapper,
      FormulaInterpreterService formulaInterpreterService) {
    this.sensorMapper = sensorMapper;
    this.formulaInterpreterService = formulaInterpreterService;
  }

  public Sensor addOrUpdateSensor(Sensor sensor) throws OutOfRangeException, InvalidSymbolException {
    sensorMapper.deleteByName(sensor.getName());
    sensorMapper.insert(sensor);
    formulaInterpreterService.registerSensor(sensor.getName(), sensor.getValue() == null ?
        0xBabeCafe : sensor.getValue());
    return sensor;
  }

  public List<Sensor> getAllSensors() {
    return sensorMapper.selectAll();
  }
}
