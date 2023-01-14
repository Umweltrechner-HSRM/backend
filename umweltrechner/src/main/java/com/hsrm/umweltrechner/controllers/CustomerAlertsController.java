package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.hsrm.umweltrechner.dao.model.CustomerAlert;
import com.hsrm.umweltrechner.dto.DtoCustomerAlert;
import com.hsrm.umweltrechner.services.CustomerAlertsService;

@RestController
@RequestMapping("/customer-alerts")
public class CustomerAlertsController {

  @Autowired
  private CustomerAlertsService customerAlertsService;

  @GetMapping()
  public List<CustomerAlert> getAllAlerts() {
    return customerAlertsService.getAllCustomerAlerts();
  }

  @DeleteMapping(value = "/{id}")
  public void deleteCustomerAlert(@PathVariable("id") String id) {
    customerAlertsService.deleteCustomerAlert(id);
  }

  @PostMapping
  public CustomerAlert insertCustomerAlert(@RequestBody DtoCustomerAlert alert) {
    Preconditions.checkArgument(StringUtils.hasText(alert.getVariableName()));
    Preconditions.checkArgument(StringUtils.hasText(alert.getPhoneNumber()) || StringUtils.hasText(alert.getEmail()));
    return customerAlertsService.insertCustomerAlert(alert);
  }

  @PutMapping(value = "/{id}")
  public CustomerAlert updateCustomerAlert(@PathVariable("id") String id,
      @RequestBody DtoCustomerAlert alert) {
    Preconditions.checkArgument(id.equalsIgnoreCase(alert.getId()));
    Preconditions.checkArgument(StringUtils.hasText(alert.getVariableName()));
    Preconditions.checkArgument(StringUtils.hasText(alert.getPhoneNumber()) || StringUtils.hasText(alert.getEmail()));
    return customerAlertsService.updateCustomerAlert(alert);
  }

}
