package com.hsrm.umweltrechner.dao.model;

import java.time.ZonedDateTime;

import com.hsrm.umweltrechner.dao.model.general.HasId;
import com.hsrm.umweltrechner.dao.model.general.HasModificationInfo;
import com.hsrm.umweltrechner.dao.model.types.DashboardComponentType;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;

import lombok.Data;

@Data
public class DashboardComponent implements HasId, HasModificationInfo {

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
    dashboardComponent.generateId();
    dashboardComponent.setType(dtoDashboardComponent.getType());
    dashboardComponent.setName(dtoDashboardComponent.getName());
    dashboardComponent.setVariable(dtoDashboardComponent.getVariable());
    dashboardComponent.setVariableColor(dtoDashboardComponent.getVariableColor());
    dashboardComponent.prepareInsert();
    return dashboardComponent;
  }

}
