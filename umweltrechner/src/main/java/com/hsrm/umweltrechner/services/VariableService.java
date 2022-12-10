package com.hsrm.umweltrechner.services;

import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.Variable;
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

  public void update(String name, double minThreshold, double maxTreshold) {
    variablesMapper.update(name, minThreshold, maxTreshold);
  }
}
