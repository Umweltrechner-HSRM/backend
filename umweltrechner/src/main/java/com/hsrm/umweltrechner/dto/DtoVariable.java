package com.hsrm.umweltrechner.dto;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class DtoVariable {

  private String name;

  private Double minThreshold;

  private Double maxThreshold;

  private ZonedDateTime lastOverThreshold;

  private List<DtoCustomerAlert> customerAlertList;
}
