package com.hsrm.umweltrechner.dao.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Dashboard {

  private String id;

  private String name;

  private ZonedDateTime createdAt;
}
