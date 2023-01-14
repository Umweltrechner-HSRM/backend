package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
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
    int updated = variablesMapper.updateThresholds(variable);
    if (updated == 0) {
      throw new NotFoundException("Variable not found");
    }
    return variable;
  }

  public void updateLastOverThreshold(String variableName, ZonedDateTime time) {
    int updated = variablesMapper.updateLastOverThreshold(variableName, time);
    if (updated == 0) {
      throw new NotFoundException("Last over threshold not updated, variable not found with name " +
          variableName);
    }
  }


  public List<Variable> getAllVariables(){
    return variablesMapper.selectAll();
  }
}
