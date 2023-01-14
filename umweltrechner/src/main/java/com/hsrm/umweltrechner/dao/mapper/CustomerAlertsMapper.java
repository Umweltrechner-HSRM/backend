package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.CustomerAlert;

@Repository
public interface CustomerAlertsMapper {

  List<CustomerAlert> selectAll();

  CustomerAlert selectByVariableName(@Param("id") String id);

  int update(CustomerAlert customerAlert);

  int insert(CustomerAlert customerAlert);

  int deleteById(@Param("id") String id);

  CustomerAlert selectById(@Param("id") String id);
}
