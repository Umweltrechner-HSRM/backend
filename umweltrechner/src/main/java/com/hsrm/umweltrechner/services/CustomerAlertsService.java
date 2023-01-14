package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.CustomerAlertsMapper;
import com.hsrm.umweltrechner.dao.model.CustomerAlert;
import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoCustomerAlert;
import com.hsrm.umweltrechner.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerAlertsService {

  @Autowired
  private CustomerAlertsMapper customerAlertsMapper;

  @Autowired
  private MailService mailService;

  @Autowired
  private VariableService variableService;

  public void deleteCustomerAlert(String id) {
    customerAlertsMapper.deleteById(id);
  }

  public CustomerAlert insertCustomerAlert(DtoCustomerAlert dtoCustomerAlert) {
    CustomerAlert customerAlert = new CustomerAlert();
    customerAlert.generateId();
    customerAlert.setVariableName(dtoCustomerAlert.getVariableName());
    customerAlert.setPhoneNumber(dtoCustomerAlert.getPhoneNumber());
    customerAlert.setEmail(dtoCustomerAlert.getEmail());
    customerAlert.prepareInsert();
    customerAlertsMapper.insert(customerAlert);
    return customerAlert;
  }

  public CustomerAlert updateCustomerAlert(DtoCustomerAlert dtoCustomerAlert) {
    CustomerAlert customerAlert = customerAlertsMapper.selectById(dtoCustomerAlert.getId());
    if (customerAlert == null) {
      throw new NotFoundException("CustomerAlert with id " + dtoCustomerAlert.getId() + " not " +
          "found");
    }
    customerAlert.setVariableName(dtoCustomerAlert.getVariableName());
    customerAlert.setPhoneNumber(dtoCustomerAlert.getPhoneNumber());
    customerAlert.setEmail(dtoCustomerAlert.getEmail());
    customerAlert.prepareUpdate();
    customerAlertsMapper.update(customerAlert);
    return customerAlert;
  }

  public List<CustomerAlert> getAllCustomerAlerts() {
    return customerAlertsMapper.selectAll();
  }

  @Transactional
  public void checkThresholds(HashMap<String, Double> interpreter) {
    List<Variable> variableList = variableService.getAllVariables();

    for (var variable : variableList) {
      boolean isDanger = false;
      Double value = interpreter.get(variable.getName());
      if (variable.getMinThreshold() != null && value <= variable.getMinThreshold()) {
        isDanger = true;
      }
      if (variable.getMaxThreshold() != null && value >= variable.getMaxThreshold()) {
        isDanger = true;
      }
      if (isDanger) {
        log.info("Threshold for variable " + variable.getName() + " is exceeded");
      }
    }

  }

}
