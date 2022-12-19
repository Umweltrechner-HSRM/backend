package com.hsrm.umweltrechner.dao.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAlertsMapper {

  void updateCustomerAlerts(String phone, String email, String variableName);

  void insert(String variableName);
}
