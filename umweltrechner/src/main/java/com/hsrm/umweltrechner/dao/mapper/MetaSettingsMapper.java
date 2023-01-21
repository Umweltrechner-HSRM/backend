package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.MetaSetting;

@Repository
public interface MetaSettingsMapper {

  List<MetaSetting> selectAll();

  int update(MetaSetting metaSetting);

  MetaSetting selectByName(@Param("name") String name);
}
