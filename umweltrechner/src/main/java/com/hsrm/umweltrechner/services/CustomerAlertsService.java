package com.hsrm.umweltrechner.services;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

  private final static int ALERT_FREQUENCY = 15; // in minutes

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
  public void processThresholds(HashMap<String, Double> interpreter) {
    List<Variable> variableList = variableService.getAllVariables();

    for (var variable : variableList) {
      boolean isDanger = isOverThreshold(interpreter.get(variable.getName()), variable);
      if (!isDanger) {
        continue;
      }
      variable.setLastOverThreshold(ZonedDateTime.now());
      variableService.updateLastOverThreshold(variable.getName(), variable.getLastOverThreshold());

      List<String> emails = customerAlertsMapper.selectByVariableName(variable.getName())
          .stream()
          .filter(x -> x.getLastNotified() == null || x.getLastNotified().isBefore(ZonedDateTime
              .now().minusMinutes(ALERT_FREQUENCY)))
          .map(x -> {
            x.setLastNotified(ZonedDateTime.now());
            customerAlertsMapper.updateLastNotified(x.getId(), x.getLastNotified());
            return x.getEmail();
          })
          .filter(StringUtils::hasText)
          .toList();
      if (!emails.isEmpty()) {
        mailService.sendWarningMails(emails, variable.getName());
      }
    }

  }

  private static boolean isOverThreshold(Double value, Variable variable) {
    boolean isDanger = false;
    if (variable.getMinThreshold() != null && value <= variable.getMinThreshold()) {
      isDanger = true;
    }
    if (variable.getMaxThreshold() != null && value >= variable.getMaxThreshold()) {
      isDanger = true;
    }
    return isDanger;
  }

}
