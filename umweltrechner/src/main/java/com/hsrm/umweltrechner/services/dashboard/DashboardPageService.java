package com.hsrm.umweltrechner.services.dashboard;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsrm.umweltrechner.dao.mapper.DashboardMapper;
import com.hsrm.umweltrechner.dao.mapper.DashboardPageMapper;
import com.hsrm.umweltrechner.dao.model.Dashboard;
import com.hsrm.umweltrechner.dao.model.DashboardPage;
import com.hsrm.umweltrechner.dto.DtoDashboard;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;
import com.hsrm.umweltrechner.dto.DtoDashboardUpdate;
import com.hsrm.umweltrechner.exceptions.NotFoundException;


@Service
public class DashboardPageService {

  @Autowired
  private DashboardMapper dashboardMapper;

  @Autowired
  private DashboardPageMapper dashboardPageMapper;

  public List<DtoDashboard> getAllDashboards() {
    return dashboardMapper.selectFullDashboards();
  }

  @Transactional
  public DtoDashboard addDashboard(DtoDashboard dtoDashboard) {
    Dashboard dashboard = new Dashboard();
    dashboard.setName(dtoDashboard.getName());
    dashboard.generateId();
    dashboard.setCreatedAt(ZonedDateTime.now());
    dashboardMapper.insert(dashboard);
    if (dtoDashboard.getComponents() != null) {
      for (int i = 0; i < dtoDashboard.getComponents().size(); i++) {
        DtoDashboardComponent component = dtoDashboard.getComponents().get(i);
        DashboardPage page = DashboardPage.from(dashboard.getId(), component.getId(), i);
        dashboardPageMapper.insert(page);
      }
    }
    return dashboardMapper.selectFullDashboardbyId(dashboard.getId());
  }


  public void deleteDashboard(String id) {
    dashboardMapper.deleteById(id);
  }

  public DtoDashboard updateDashboard(DtoDashboardUpdate dashboard) {
    Dashboard oldDashboard = dashboardMapper.selectById(dashboard.getId());
    if (oldDashboard == null) {
      throw new NotFoundException("Dashboard not found with id=" + dashboard.getId());
    }
    oldDashboard.setName(dashboard.getName());
    dashboardMapper.update(oldDashboard);
    dashboardPageMapper.deleteByDashboardId(dashboard.getId());

    if (dashboard.getComponents() != null) {
      for (int i = 0; i < dashboard.getComponents().size(); i++) {
        DashboardPage page = DashboardPage.from(dashboard.getId(), dashboard.getComponents().get(i), i);
        dashboardPageMapper.insert(page);
      }
    }
    return dashboardMapper.selectFullDashboardbyId(dashboard.getId());

  }


}
