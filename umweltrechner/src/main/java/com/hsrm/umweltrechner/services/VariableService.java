package com.hsrm.umweltrechner.services;

import java.util.List;

import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VariableService {

  private final VariableMapper variablesMapper;

  @Autowired
  public VariableService(VariableMapper variablesMapper) {
    this.variablesMapper = variablesMapper;
  }

  public Variable update(Variable variable) {
    int updated = variablesMapper.updateVariable(variable);
    if (updated == 0) {
      throw new NotFoundException("Variable not found");
    }
    return variable;
  }

  public List<Variable> getAllVariables(){
    return variablesMapper.selectAll();
  }
}
