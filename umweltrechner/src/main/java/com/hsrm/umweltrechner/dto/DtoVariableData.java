package com.hsrm.umweltrechner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoVariableData {
  private String variableName;
  private Double value;
  private Long timestamp;
}
