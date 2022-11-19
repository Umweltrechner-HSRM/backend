package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Sensor;


@Repository
public interface SensorMapper {

  List<Sensor> selectAll();

  int selectByName(@Param("name") String name);

  int deleteByName(@Param("name") String name);

  int insert(Sensor sensor);
}
