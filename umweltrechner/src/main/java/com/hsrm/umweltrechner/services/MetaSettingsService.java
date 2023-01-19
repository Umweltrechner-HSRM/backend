package com.hsrm.umweltrechner.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.MetaSettingsMapper;
import com.hsrm.umweltrechner.dao.model.MetaSetting;
import com.hsrm.umweltrechner.dao.model.types.MetaSettingKey;
import com.hsrm.umweltrechner.dto.DtoMetaSettings;

@Service
public class MetaSettingsService {

  @Autowired
  private MetaSettingsMapper metaSettingsMapper;

  public DtoMetaSettings getMetaSettings() {
    DtoMetaSettings dtoMetaSettings = new DtoMetaSettings();
    List<MetaSetting> metaSettings = metaSettingsMapper.selectAll();
    for (MetaSetting metaSetting : metaSettings) {
      dtoMetaSettings.getMetaSettings().put(metaSetting.getName(), metaSetting.getValue());
    }
    return dtoMetaSettings;
  }

  public String getDefaultMail() {
    return metaSettingsMapper.selectByName(String.valueOf(MetaSettingKey.DEFAULT_MAIL)).getValue();
  }

  public Integer getMailFrequency() {
    return Integer.valueOf(metaSettingsMapper.selectByName(String.valueOf(MetaSettingKey.MAIL_FREQUENCY)).getValue());
  }

  @Transactional
  public void update(DtoMetaSettings dtoMetaSettings) {
    for (MetaSettingKey metaSettingKey : dtoMetaSettings.getMetaSettings().keySet()) {
      MetaSettingKey key = MetaSettingKey.getMetaSettingKey(metaSettingKey.name());
      if (key == null) {
        throw new IllegalArgumentException("MetaSettingKey " + metaSettingKey.name() + " is not valid");
      }
      MetaSetting metaSetting = new MetaSetting();
      metaSetting.setName(MetaSettingKey.valueOf(key.name()));
      metaSetting.setValue(dtoMetaSettings.getMetaSettings().get(metaSettingKey));
      metaSettingsMapper.update(metaSetting);
    }
  }
}
