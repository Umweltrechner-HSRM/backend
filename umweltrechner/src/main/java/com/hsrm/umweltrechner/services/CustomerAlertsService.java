package com.hsrm.umweltrechner.services;

import java.util.List;

import com.hsrm.umweltrechner.dao.mapper.CustomerAlertsMapper;
import com.hsrm.umweltrechner.dao.model.CustomerAlert;
import com.hsrm.umweltrechner.dto.DtoCustomerAlert;
import com.hsrm.umweltrechner.exceptions.NotFoundException;

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
      throw new NotFoundException("CustomerAlert with id " + dtoCustomerAlert.getId() + " not found");
    }
    customerAlert.setVariableName(dtoCustomerAlert.getVariableName());
    customerAlert.setPhoneNumber(dtoCustomerAlert.getPhoneNumber());
    customerAlert.setEmail(dtoCustomerAlert.getEmail());
    customerAlert.prepareUpdate();
    customerAlertsMapper.update(customerAlert);
    return customerAlert;
  }

  public List<CustomerAlert> getAllCustomerAlerts(){
    return customerAlertsMapper.selectAll();
  }

  public CustomerAlert getAlertsByVariableName(String id){
    return customerAlertsMapper.selectByVariableName(id);
  }
}
