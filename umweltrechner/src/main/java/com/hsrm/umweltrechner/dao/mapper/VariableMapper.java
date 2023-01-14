package com.hsrm.umweltrechner.dao.mapper;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Variable;
import com.hsrm.umweltrechner.dto.DtoVariable;

@Repository
public interface VariableMapper {

  List<Variable> selectAll();

  List<DtoVariable> selectAllWithCustomerAlerts();


  int deleteByName(String name);

  int insert(Variable variable);

  int updateThresholds(Variable variable);

  int updateLastOverThreshold(@Param("name") String name,
      @Param("lastOverThreshold") ZonedDateTime lastOverThreshold);


}
