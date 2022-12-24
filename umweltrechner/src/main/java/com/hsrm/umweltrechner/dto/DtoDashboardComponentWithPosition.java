package com.hsrm.umweltrechner.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DtoDashboardComponentWithPosition extends DtoDashboardComponent {

  private Integer position;
}
