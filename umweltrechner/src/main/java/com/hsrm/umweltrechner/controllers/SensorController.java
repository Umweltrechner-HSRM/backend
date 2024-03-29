package com.hsrm.umweltrechner.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.hsrm.umweltrechner.dao.model.Sensor;
import com.hsrm.umweltrechner.services.SensorService;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;

@RestController
@RequestMapping("/sensor")
public class SensorController {

  @Autowired
  SensorService sensorService;


  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<Sensor> getSensorNames(){
    return sensorService.getAllSensors();
  }

  @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public Sensor addSensor(@RequestBody Sensor sensor) throws OutOfRangeException, InvalidSymbolException {
    Preconditions.checkArgument(StringUtils.hasText(sensor.getName()));
    return sensorService.addOrUpdateSensor(sensor);
  }


}
