package com.hsrm.umweltrechner.dto;

import java.util.List;

import lombok.Data;

@Data
public class DtoDashboard {

  private String id;

  private String name;

  private List<DtoDashboardComponentWithPosition> components;


}
