package com.hsrm.umweltrechner.dao.model;

import lombok.Data;

@Data
public class History {
  private String variableName;
  private Double value;
  private Long timestamp;
}
