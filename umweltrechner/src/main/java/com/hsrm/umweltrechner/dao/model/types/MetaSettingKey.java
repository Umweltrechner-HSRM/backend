package com.hsrm.umweltrechner.dao.model.types;

public enum MetaSettingKey {
  DEFAULT_MAIL,
  MAIL_FREQUENCY;

  public static MetaSettingKey getMetaSettingKey(String metaSettingKey) {
    for (MetaSettingKey key : MetaSettingKey.values()) {
      if (key.name().equalsIgnoreCase(metaSettingKey)) {
        return key;
      }
    }
    return null;
  }

}
