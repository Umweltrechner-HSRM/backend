package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Sensor;


@Repository
public interface SensorMapper {

  List<Sensor> selectAll();

}
