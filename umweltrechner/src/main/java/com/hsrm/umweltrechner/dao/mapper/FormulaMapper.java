package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hsrm.umweltrechner.dao.model.Formula;

@Repository
public interface FormulaMapper {

  List<Formula> selectAll();

  int insert(Formula formula);

  int update(Formula f);

  Formula selectById(@Param("id") String formulaId);
}
