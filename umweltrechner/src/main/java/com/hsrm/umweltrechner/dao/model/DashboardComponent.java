package com.hsrm.umweltrechner.dao.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.hsrm.umweltrechner.dao.model.types.DashboardComponentType;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.util.CurrentSession;

import lombok.Data;

@Data
public class DashboardComponent {

  private String id;

  private String name;

  private DashboardComponentType type;

  private String variable;

  private String variableColor;

  private ZonedDateTime createdAt;

  private String createdBy;

  private ZonedDateTime changedAt;

  private String changedBy;

  public static DashboardComponent from(DtoDashboardComponent dtoDashboardComponent) {
    DashboardComponent dashboardComponent = new DashboardComponent();
    dashboardComponent.setId(UUID.randomUUID().toString());
    dashboardComponent.setType(dtoDashboardComponent.getType());
    dashboardComponent.setName(dtoDashboardComponent.getName());
    dashboardComponent.setVariable(dtoDashboardComponent.getVariable());
    dashboardComponent.setVariableColor(dtoDashboardComponent.getVariableColor());
    ZonedDateTime now = ZonedDateTime.now();
    dashboardComponent.setCreatedAt(now);
    dashboardComponent.setCreatedBy(CurrentSession.getCurrentUserName());
    dashboardComponent.setChangedAt(now);
    dashboardComponent.setChangedBy(CurrentSession.getCurrentUserName());
    return dashboardComponent;
  }

}
