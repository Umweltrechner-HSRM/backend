package com.hsrm.umweltrechner.dao.model;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.general.HasId;
import com.hsrm.umweltrechner.dao.model.general.HasModificationInfo;

import lombok.Data;

@Data
public class Formula implements HasId, HasModificationInfo {

  private String id;

  private String formula;

  private ZonedDateTime createdAt;

  private String createdBy;

  private ZonedDateTime changedAt;

  private String changedBy;

}
