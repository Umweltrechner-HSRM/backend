package com.hsrm.umweltrechner.controllers;

import java.util.List;

import javax.annotation.Resource;

import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.services.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

@Controller
@RequestMapping("/variable")
public class VariableController {

  @Resource
  private VariableService variableService;

  @Autowired
  private FormulaInterpreterService formulaInterpreterService;

  @GetMapping
  public ResponseEntity<List<String>> getVariableNames(){
    return ResponseEntity.ok(formulaInterpreterService.getVariableNames());
  }

  // Put endpoint to update thresholds
  @PutMapping(value = "/updateThreshold")
  public ResponseEntity<String> updateVariable(@RequestBody Variable variable){
    String name = variable.getName();
    double minThreshold = variable.getMinThreshold();
    double maxThreshold = variable.getMaxThreshold();
    variableService.update(name, minThreshold, maxThreshold);
    return ResponseEntity.ok("updated variable");
  }


}
