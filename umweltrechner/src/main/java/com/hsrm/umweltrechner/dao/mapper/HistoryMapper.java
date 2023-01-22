package com.hsrm.umweltrechner.dao.mapper;

import java.util.List;

import com.hsrm.umweltrechner.dao.model.History;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryMapper {

  int insert(DtoVariableData dtoVariableData);

  List<History> selectAll();

  List<History> selectAllByName(String variableName);
}
