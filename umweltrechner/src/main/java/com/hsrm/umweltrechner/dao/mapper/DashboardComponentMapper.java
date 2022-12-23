package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.DashboardComponent;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoDashboardComponent;

@Repository
public interface DashboardComponentMapper {

  List<DtoDashboardComponent> selectAll();

  int deleteById(@Param("id") String id);

  int insert(DashboardComponent dashboardComponent);

  DashboardComponent selectById(@Param("id") String id);

  void update(DashboardComponent dashboardComponent);
}
