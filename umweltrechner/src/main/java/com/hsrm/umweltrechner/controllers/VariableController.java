package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

@Controller
@RequestMapping("/variable")
public class VariableController {

  @Autowired
  private FormulaInterpreterService formulaInterpreterService;

  @GetMapping
  public ResponseEntity<List<String>> getVariables(){
    return ResponseEntity.ok(formulaInterpreterService.getVariableNames());
  }

  // Post or Patch endpoint to update thresholds



}
