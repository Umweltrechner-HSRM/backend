package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

@RestController
@RequestMapping("/variable")
public class VariableController {

  @Autowired
  private FormulaInterpreterService formulaInterpreterService;

  @GetMapping
  public ResponseEntity<List<String>> getSensorNames(){
    return ResponseEntity.ok(formulaInterpreterService.getVariableNames());
  }



}
