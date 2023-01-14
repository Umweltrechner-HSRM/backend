package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
import java.util.List;

import com.hsrm.umweltrechner.dao.mapper.CustomerAlertsMapper;
import com.hsrm.umweltrechner.dao.mapper.VariableMapper;
import com.hsrm.umweltrechner.dao.model.CustomerAlert;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoCustomerAlert;
import com.hsrm.umweltrechner.dto.DtoVariable;
import com.hsrm.umweltrechner.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VariableService {

  @Autowired
  private VariableMapper variablesMapper;

  @Autowired
  private CustomerAlertsMapper customerAlertsMapper;

  public DtoVariable update(DtoVariable variable) {
    Variable v = new Variable();
    v.setName(variable.getName());
    v.setMinThreshold(variable.getMinThreshold());
    v.setMaxThreshold(variable.getMaxThreshold());

    int updated = variablesMapper.updateThresholds(v);
    if (updated == 0) {
      throw new NotFoundException("Variable not found");
    }
    customerAlertsMapper.deleteByVariableName(variable.getName());
    for (DtoCustomerAlert x : variable.getCustomerAlertList()) {
      CustomerAlert ca = new CustomerAlert();
      ca.generateId();
      ca.setVariableName(variable.getName());
      ca.setEmail(x.getEmail());
      ca.setPhoneNumber(x.getPhoneNumber());
      customerAlertsMapper.insert(ca);
    }
    return variablesMapper.selectAllWithCustomerAlertsByName(variable.getName());
  }

  public void updateLastOverThreshold(String variableName, ZonedDateTime time) {
    int updated = variablesMapper.updateLastOverThreshold(variableName, time);
    if (updated == 0) {
      throw new NotFoundException("Last over threshold not updated, variable not found with name " +
          variableName);
    }
  }

  public List<DtoVariable> selectAllWithCustomerAlerts() {
    return variablesMapper.selectAllWithCustomerAlerts();
  }

  public List<Variable> selectAll() {
    return variablesMapper.selectAll();
  }



}
