package com.hsrm.umweltrechner.dao.model;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.general.HasId;
import com.hsrm.umweltrechner.dao.model.general.HasModificationInfo;

import lombok.Data;

@Data
public class CustomerAlert implements HasId {

  private String id;

  private String variableName;

  private String phoneNumber;

  private String email;

  private ZonedDateTime lastNotified;

}
