package com.hsrm.umweltrechner.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsrm.umweltrechner.dto.DtoMetaSettings;
import com.hsrm.umweltrechner.services.MetaSettingsService;

@RestController
@RequestMapping("/meta-settings")
public class MetaSettingsController {

  @Autowired
  private MetaSettingsService metaSettingsService;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public DtoMetaSettings getMetaSettings() {
    return metaSettingsService.getMetaSettings();
  }

  @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('master')")
  public void updateMetaSettings(@RequestBody DtoMetaSettings dtoMetaSettings) {
    metaSettingsService.update(dtoMetaSettings);
  }

}
