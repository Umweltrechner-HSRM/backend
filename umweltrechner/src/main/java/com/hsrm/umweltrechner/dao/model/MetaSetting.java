package com.hsrm.umweltrechner.dao.model;

import com.hsrm.umweltrechner.dao.model.types.MetaSettingKey;

import lombok.Data;

@Data
public class MetaSetting {

  private MetaSettingKey name;

  private String value;
}
