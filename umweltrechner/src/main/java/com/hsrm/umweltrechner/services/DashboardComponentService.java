package com.hsrm.umweltrechner.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.DashboardComponentMapper;
import com.hsrm.umweltrechner.dao.model.DashboardComponent;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.exceptions.NotFoundException;

@Service
public class DashboardComponentService {

  @Autowired
  private DashboardComponentMapper dashboardComponentMapper;
  @Autowired
  private FormulaInterpreterService formulaInterpreterService;

  public DtoDashboardComponent addComponent(DtoDashboardComponent dtoDashboardComponent) {
    if (!formulaInterpreterService.variableExists(dtoDashboardComponent.getVariable())) {
      throw new IllegalArgumentException("Variable does not exist name=" + dtoDashboardComponent.getVariable());
    }
    DashboardComponent dashboardComponent = DashboardComponent.from(dtoDashboardComponent);
    dashboardComponent.prepareInsert();
    dashboardComponentMapper.insert(dashboardComponent);
    return DtoDashboardComponent.from(dashboardComponent);
  }

  public void deleteComponent(String id) {
    dashboardComponentMapper.deleteById(id);
  }

  @Transactional
  public DtoDashboardComponent updateComponent(String id,
      DtoDashboardComponent dtoDashboardComponent) {
    if (!formulaInterpreterService.variableExists(dtoDashboardComponent.getVariable())) {
      throw new IllegalArgumentException("Variable does not exist name=" + dtoDashboardComponent.getVariable());
    }

    DashboardComponent dashboardComponent = new DashboardComponent();
    dashboardComponent.setId(id);
    dashboardComponent.setType(dtoDashboardComponent.getType());
    dashboardComponent.setName(dtoDashboardComponent.getName());
    dashboardComponent.setVariable(dtoDashboardComponent.getVariable());
    dashboardComponent.setVariableColor(dtoDashboardComponent.getVariableColor());
    dashboardComponent.setStroke(dtoDashboardComponent.getStroke());
    dashboardComponent.prepareUpdate();

    int updated = dashboardComponentMapper.update(dashboardComponent);
    if (updated == 0) {
      throw new NotFoundException("DashboardComponent not found with id: " + id);
    }

    return DtoDashboardComponent.from(dashboardComponent);
  }

  public List<DashboardComponent> getDashboardComponents() {
    return dashboardComponentMapper.selectAll();
  }
}
