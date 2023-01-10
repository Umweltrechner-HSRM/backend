package com.hsrm.umweltrechner.dao.model.general;

import java.util.UUID;

public interface HasId {

    public String getId();

    public void setId(String id);

    public default void generateId() {
        setId(UUID.randomUUID().toString());
    }
}
