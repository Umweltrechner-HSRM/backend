package com.hsrm.umweltrechner.dao.model;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.general.HasId;
import com.hsrm.umweltrechner.dao.model.general.HasModificationInfo;

import lombok.Data;

@Data
public class CustomerAlert implements HasId, HasModificationInfo {

  private String id;

  private String variableName;

  private String phoneNumber;

  private String email;

  private ZonedDateTime lastNotified;

  private ZonedDateTime createdAt;

  private String createdBy;

  private ZonedDateTime changedAt;

  private String changedBy;

}
