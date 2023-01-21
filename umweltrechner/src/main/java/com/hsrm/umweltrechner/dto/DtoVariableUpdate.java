package com.hsrm.umweltrechner.dto;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class DtoVariableUpdate {

  private String name;

  private Double minThreshold;

  private Double maxThreshold;

  private List<String> emailList;
}
