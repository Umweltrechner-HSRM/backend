package com.hsrm.umweltrechner.dao.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Dashboard;
import com.hsrm.umweltrechner.dto.DtoDashboard;


@Repository
public interface DashboardMapper {

  int deleteById(@Param("id") String id);

  List<DtoDashboard> selectFullDashboards();

  DtoDashboard selectFullDashboardbyId(@Param("id") String id);

  Dashboard selectById(@Param("id") String id);

  int insert(Dashboard dashboard);

  void update(Dashboard dashboard);
}
