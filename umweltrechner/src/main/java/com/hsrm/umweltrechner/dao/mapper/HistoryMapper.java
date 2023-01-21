package com.hsrm.umweltrechner.dao.mapper;

import com.hsrm.umweltrechner.dto.DtoVariableData;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryMapper {

  int insert(DtoVariableData dtoVariableData);
}
