package com.hsrm.umweltrechner.dao.model;
import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.types.VariableType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variable {
  private String name;
  private Double minThreshold;
  private Double maxThreshold;
  private VariableType type;
  private ZonedDateTime lastOverThreshold;
}
