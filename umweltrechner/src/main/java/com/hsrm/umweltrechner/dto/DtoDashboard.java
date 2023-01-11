package com.hsrm.umweltrechner.dto;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class DtoDashboard {

  private String id;

  private String name;

  private ZonedDateTime createdAt;

  private List<DtoDashboardComponent> components;


}
