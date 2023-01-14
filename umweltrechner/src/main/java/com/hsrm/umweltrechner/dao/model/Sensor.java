package com.hsrm.umweltrechner.dao.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Sensor {

  private String name;

  private String location;

  private String unit;

  private String description;

  private Double value;

  private Timestamp createdAt;

}
