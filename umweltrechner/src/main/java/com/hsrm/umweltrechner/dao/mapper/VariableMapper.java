package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Variable;

@Repository
public interface VariableMapper {

  List<Variable> selectAll();
  int deleteByName(String name);

  int insert(Variable variable);

  void updateVariable(String name, double minThreshold, double maxThreshold);

}
