package com.hsrm.umweltrechner.dto;

import java.util.List;

import lombok.Data;

@Data
public class DtoDashboardUpdate {

  private String id;

  private String name;

  private List<String> components;

}
