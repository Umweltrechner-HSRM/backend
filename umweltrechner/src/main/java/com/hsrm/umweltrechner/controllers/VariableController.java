package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoVariable;
import com.hsrm.umweltrechner.services.VariableService;

@RestController
@RequestMapping("/variable")
public class VariableController {

  @Autowired
  private VariableService variableService;


  @GetMapping()
  public List<DtoVariable> getAllVariablesDto() {
    return variableService.selectAllWithCustomerAlerts();
  }

  @GetMapping(value = "/getAllVariables")
  public List<Variable> getAllVariables() {
    return variableService.selectAll();
  }

  @PutMapping(value = "/{name}")
  public DtoVariable update(@PathVariable("name") String name,
      @RequestBody DtoVariable variable) {
    Preconditions.checkArgument(name.equals(variable.getName()));
    if (variable.getMinThreshold() != null && variable.getMaxThreshold() != null) {
      Preconditions.checkArgument(variable.getMinThreshold() <= variable.getMaxThreshold());
    }
    return variableService.update(variable);
  }


}
