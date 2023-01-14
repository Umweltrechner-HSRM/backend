package com.hsrm.umweltrechner.dao.mapper;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.CustomerAlert;

@Repository
public interface CustomerAlertsMapper {

  List<CustomerAlert> selectAll();

  List<CustomerAlert> selectByVariableName(@Param("variableName") String variableName);

  int update(CustomerAlert customerAlert);

  int insert(CustomerAlert customerAlert);

  int deleteById(@Param("id") String id);

  int updateLastNotified(@Param("id") String id, @Param("lastNotified") ZonedDateTime lastNotified);

  CustomerAlert selectById(@Param("id") String id);
}
