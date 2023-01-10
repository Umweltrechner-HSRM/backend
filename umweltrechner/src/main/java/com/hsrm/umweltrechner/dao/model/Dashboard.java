package com.hsrm.umweltrechner.dao.model;

import lombok.Data;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.general.HasId;

@Data
public class Dashboard implements HasId {

  private String id;

  private String name;

  private ZonedDateTime createdAt;
}
