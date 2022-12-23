package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.hsrm.umweltrechner.dao.model.DashboardComponent;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  @Autowired
  private DashboardService dashboardService;


  @GetMapping("/components")
  @PreAuthorize("hasRole('admin')")
  public List<DtoDashboardComponent> getDashboardComponents() {
    return dashboardService.getDashboardComponents();
  }

  @PostMapping("/components")
  @PreAuthorize("hasRole('admin')")
  public DtoDashboardComponent addComponent(@RequestBody DtoDashboardComponent dtoDashboardComponent) {
    return dashboardService.addComponent(dtoDashboardComponent);
  }

  @DeleteMapping("/components/{id}")
  @PreAuthorize("hasRole('admin')")
  public void deleteComponent(@PathVariable("id") String id) {
    dashboardService.deleteComponent(id);
  }

  @PutMapping("/components/{id}")
  @PreAuthorize("hasRole('admin')")
  public DtoDashboardComponent updateComponent(@PathVariable("id") String id,
      @RequestBody DtoDashboardComponent component) {
    Preconditions.checkArgument(id.equalsIgnoreCase(component.getId()));
    return dashboardService.updateComponent(id, component);
  }

}
