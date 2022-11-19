package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hsrm.umweltrechner.dao.model.Sensor;
import com.hsrm.umweltrechner.services.SensorService;

@Controller
@RequestMapping("/sensor")
public class SensorController {

  @Autowired
  SensorService sensorService;


  @GetMapping
  public ResponseEntity<List<Sensor>> getSensorNames(){
    return ResponseEntity.ok(sensorService.getAllSensors());
  }

  @PutMapping
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<Sensor> addSensor(@RequestBody Sensor sensor){
    return ResponseEntity.ok(sensorService.addOrUpdateSensor(sensor));
  }


}
