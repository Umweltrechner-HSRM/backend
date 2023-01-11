package com.hsrm.umweltrechner.dto;


import com.hsrm.umweltrechner.dao.model.DashboardComponent;
import com.hsrm.umweltrechner.dao.model.types.DashboardComponentStrokeType;
import com.hsrm.umweltrechner.dao.model.types.DashboardComponentType;

import lombok.Data;

@Data
public class DtoDashboardComponent {

  private String id;

  private String name;

  private DashboardComponentType type;

  private String variable;

  private String variableColor;

  private DashboardComponentStrokeType stroke;


  public static DtoDashboardComponent from(DashboardComponent dashboardComponent) {
    DtoDashboardComponent dtoDashboardComponent = new DtoDashboardComponent();
    dtoDashboardComponent.setId(dashboardComponent.getId());
    dtoDashboardComponent.setType(dashboardComponent.getType());
    dtoDashboardComponent.setName(dashboardComponent.getName());
    dtoDashboardComponent.setVariable(dashboardComponent.getVariable());
    dtoDashboardComponent.setVariableColor(dashboardComponent.getVariableColor());
    dtoDashboardComponent.setStroke(dashboardComponent.getStroke());
    return dtoDashboardComponent;
  }

}
