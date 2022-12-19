package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Variable;

@Repository
public interface VariableMapper {

  List<Variable> selectAll();
  void deleteByName(String name);

  void insertNewVariableWithName(String name);

  int insert(String name, double minThreshold, double maxThreshold);

  void updateVariable(String name, double minThreshold, double maxThreshold);

}
