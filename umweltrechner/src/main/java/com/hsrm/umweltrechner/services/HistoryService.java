package com.hsrm.umweltrechner.services;

import javax.annotation.Resource;

import com.hsrm.umweltrechner.dao.mapper.HistoryMapper;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HistoryService {

  @Autowired
  private HistoryMapper historyMapper;

  public void insertVariable(DtoVariableData dtoVariableData){
    historyMapper.insert(dtoVariableData);
  }

}
