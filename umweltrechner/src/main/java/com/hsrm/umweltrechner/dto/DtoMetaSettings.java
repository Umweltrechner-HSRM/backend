package com.hsrm.umweltrechner.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.hsrm.umweltrechner.dao.model.types.MetaSettingKey;

import lombok.Data;

@Data
public class DtoMetaSettings {

  private final Map<MetaSettingKey, String> metaSettings = new HashMap<>();

  @JsonAnyGetter
  public Map<MetaSettingKey, String> getMetaSettings() {
    return metaSettings;
  }

  @JsonAnySetter
  public void setMetaSettings(String key, String value) {
    metaSettings.put(MetaSettingKey.valueOf(key), value);
  }
}
