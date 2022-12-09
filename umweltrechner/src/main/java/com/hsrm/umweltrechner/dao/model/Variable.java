package com.hsrm.umweltrechner.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Variable {
  private String name;
  private Double minThreshold;
  private Double maxThreshold;
}
