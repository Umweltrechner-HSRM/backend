package com.hsrm.umweltrechner.dao.model.general;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.util.CurrentSession;


public interface HasModificationInfo extends HasCreationInfo {

    public void setChangedAt(ZonedDateTime changedAt);

    public ZonedDateTime getChangedAt();

    public void setChangedBy(String changedBy);

    public String getChangedBy();

    @Override
    public default void prepareInsert() {
        ZonedDateTime now = ZonedDateTime.now();
        setChangedAt(now);
        setChangedBy(CurrentSession.getCurrentUserName());
        setCreatedAt(now);
        setCreatedBy(CurrentSession.getCurrentUserName());
    }

    public default void prepareUpdate() {
        ZonedDateTime now = ZonedDateTime.now();
        setChangedAt(now);
        setChangedBy(CurrentSession.getCurrentUserName());
    }

}
