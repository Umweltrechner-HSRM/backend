package com.hsrm.umweltrechner.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DtoVariableWithCustomerAlerts extends DtoVariable {

  private List<DtoCustomerAlert> customerAlertList;
}
