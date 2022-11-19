package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

@Controller
@RequestMapping("/sensor")
public class SensorDataController {

  @Autowired
  FormulaInterpreterService formulaInterpreterService;


  @GetMapping
  public ResponseEntity<List<String>> getSensorNames(){
    return ResponseEntity.ok(formulaInterpreterService.getVariables().keySet().stream().toList());
  }

  @PostMapping("/add")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<String> addSensor(){
    return ResponseEntity.ok("Sensor added");
  }


}
