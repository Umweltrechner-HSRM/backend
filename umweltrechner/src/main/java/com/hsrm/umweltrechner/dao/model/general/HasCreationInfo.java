package com.hsrm.umweltrechner.dao.model.general;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.util.CurrentSession;


public interface HasCreationInfo extends Serializable {

  void setCreatedAt(ZonedDateTime createdAt);

  ZonedDateTime getCreatedAt();

  void setCreatedBy(String createdBy);

  String getCreatedBy();

  default void prepareInsert() {
    ZonedDateTime now = ZonedDateTime.now();
    setCreatedAt(now);
    setCreatedBy(CurrentSession.getCurrentUserName());
  }
}
