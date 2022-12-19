package com.hsrm.umweltrechner.services;

import com.hsrm.umweltrechner.dao.mapper.CustomerAlertsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerAlertsService {

  private final CustomerAlertsMapper customerAlertsMapper;

  @Autowired
  public CustomerAlertsService(CustomerAlertsMapper customerAlertsMapper) {
    this.customerAlertsMapper = customerAlertsMapper;
  }

  public void replace(String phone, String email, String name){
    customerAlertsMapper.updateCustomerAlerts(phone, email, name);
  }

  public void insert(String name){
    customerAlertsMapper.insert(name);
  }
}
