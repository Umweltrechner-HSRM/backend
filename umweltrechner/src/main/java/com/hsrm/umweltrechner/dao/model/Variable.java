package com.hsrm.umweltrechner.dao.model;
import java.time.ZonedDateTime;

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
  private ZonedDateTime lastOverThreshold;
}
