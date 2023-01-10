package com.hsrm.umweltrechner.controllers;

import java.util.List;

import javax.annotation.Resource;

import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoVariable;
import com.hsrm.umweltrechner.services.CustomerAlertsService;
import com.hsrm.umweltrechner.services.VariableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

@RestController
@RequestMapping("/variable")
public class VariableController {

  @Resource
  private VariableService variableService;

  @Resource
  private CustomerAlertsService customerAlertsService;

  @Resource
  private FormulaInterpreterService formulaInterpreterService;

  @GetMapping
  public ResponseEntity<List<String>> getVariableNames(){
    return ResponseEntity.ok(formulaInterpreterService.getVariableNames());
  }

  @GetMapping(value = "/getAllVariables")
  public ResponseEntity<List<Variable>> getAllVariables(){
    return ResponseEntity.ok(variableService.getAllVariables());
  }
  
  @PutMapping(value = "/updateThreshold")
  public ResponseEntity<String> updateVariable(@RequestBody DtoVariable variable){
    List<Variable> variables = variable.getVariables();
    for (var x : variables){
      variableService.update(x.getName(), x.getMinThreshold(), x.getMaxThreshold());
      customerAlertsService.replace(variable.getMobile(), variable.getMail(), x.getName());
    }
    return ResponseEntity.ok("updated variable");
  }


}
