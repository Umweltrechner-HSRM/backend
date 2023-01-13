package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.hsrm.umweltrechner.dto.DtoDashboard;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.dto.DtoDashboardUpdate;
import com.hsrm.umweltrechner.services.DashboardComponentService;
import com.hsrm.umweltrechner.services.DashboardPageService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  @Autowired
  private DashboardComponentService dashboardComponentService;
  @Autowired
  private DashboardPageService dashboardPageService;

  @GetMapping
  public List<DtoDashboard> getDashboards() {
    return dashboardPageService.getAllDashboards();
  }

  @PostMapping
  @PreAuthorize("hasRole('admin')")
  public DtoDashboard addDashboard(@RequestBody DtoDashboard dashboard) {
    Preconditions.checkNotNull(dashboard);
    Preconditions.checkArgument(!dashboard.getName().isEmpty(), "name must not be null or empty");
    return dashboardPageService.addDashboard(dashboard);
  }


  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('admin')")
  public void deleteDashboard(@PathVariable("id") String id) {
    dashboardPageService.deleteDashboard(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('admin')")
  public DtoDashboard updateDashboard(@PathVariable("id") String id,
      @RequestBody DtoDashboardUpdate dashboard) {
    Preconditions.checkArgument(id.equalsIgnoreCase(dashboard.getId()));
    return dashboardPageService.updateDashboard(dashboard);
  }


  @GetMapping("/components")
  public List<DashboardComponent> getDashboardComponents() {
    return dashboardComponentService.getDashboardComponents();
  }

  @PostMapping("/components")
  @PreAuthorize("hasRole('admin')")
  public DtoDashboardComponent addComponent(@RequestBody DtoDashboardComponent dtoDashboardComponent) {
    return dashboardComponentService.addComponent(dtoDashboardComponent);
  }

  @DeleteMapping("/components/{id}")
  @PreAuthorize("hasRole('admin')")
  public void deleteComponent(@PathVariable("id") String id) {
    dashboardComponentService.deleteComponent(id);
  }

  @PutMapping("/components/{id}")
  @PreAuthorize("hasRole('admin')")
  public DtoDashboardComponent updateComponent(@PathVariable("id") String id,
      @RequestBody DtoDashboardComponent component) {
    Preconditions.checkArgument(id.equalsIgnoreCase(component.getId()));
    return dashboardComponentService.updateComponent(id, component);
  }

}
