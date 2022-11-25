package com.hsrm.umweltrechner.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Variable {
  private String name;
  private Double minThreshold;
  private Double maxThreshold;
}
