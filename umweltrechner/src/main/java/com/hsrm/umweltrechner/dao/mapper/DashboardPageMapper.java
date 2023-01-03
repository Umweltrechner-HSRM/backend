package com.hsrm.umweltrechner.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.DashboardPage;

@Repository
public interface DashboardPageMapper {

  int insert(DashboardPage dashboardPage);

  int deleteByDashboardId(@Param("dashboardId") String dashboardId);
}
