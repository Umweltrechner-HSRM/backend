package com.hsrm.umweltrechner.services.dashboard;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.DashboardComponentMapper;
import com.hsrm.umweltrechner.dao.model.DashboardComponent;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.exceptions.NotFoundException;
import com.hsrm.umweltrechner.util.CurrentSession;

@Service
public class DashboardComponentService {

  @Autowired
  private DashboardComponentMapper dashboardComponentMapper;

  public DtoDashboardComponent addComponent(DtoDashboardComponent dtoDashboardComponent) {
    DashboardComponent dashboardComponent = DashboardComponent.from(dtoDashboardComponent);
    dashboardComponentMapper.insert(dashboardComponent);
    return DtoDashboardComponent.from(dashboardComponent);
  }

  public void deleteComponent(String id) {
    dashboardComponentMapper.deleteById(id);
  }

  @Transactional
  public DtoDashboardComponent updateComponent(String id, DtoDashboardComponent dtoDashboardComponent) {
    DashboardComponent oldDashboardComponent = dashboardComponentMapper.selectById(id);

    if(oldDashboardComponent == null) {
      throw new NotFoundException("DashboardComponent not found with id: " + id);
    }

    DashboardComponent dashboardComponent = new DashboardComponent();
    dashboardComponent.setId(id);
    dashboardComponent.setType(dtoDashboardComponent.getType());
    dashboardComponent.setName(dtoDashboardComponent.getName());
    dashboardComponent.setVariable(dtoDashboardComponent.getVariable());
    dashboardComponent.setVariableColor(dtoDashboardComponent.getVariableColor());
    dashboardComponent.setChangedAt(ZonedDateTime.now());
    dashboardComponent.setChangedBy(CurrentSession.getCurrentUserName());
    dashboardComponentMapper.update(dashboardComponent);

    return DtoDashboardComponent.from(dashboardComponent);
  }

  public List<DtoDashboardComponent> getDashboardComponents() {
    return dashboardComponentMapper.selectAll();
  }
}
