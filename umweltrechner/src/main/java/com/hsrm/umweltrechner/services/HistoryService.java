package com.hsrm.umweltrechner.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.annotation.Resource;

import com.hsrm.umweltrechner.dao.mapper.HistoryMapper;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HistoryService {

  @Autowired
  private HistoryMapper historyMapper;

  // Queue
  Queue<DtoVariableData> variableDataQueue = new ConcurrentLinkedDeque<>();

  public void insertVariable(DtoVariableData dtoVariableData){
    historyMapper.insert(dtoVariableData);
  }

  public void addItem(DtoVariableData dtoVariableData){
    variableDataQueue.add(dtoVariableData);
  }

  @Scheduled(fixedRateString = "60000")
  public void insertAllVariablesInQueue(){
    if (variableDataQueue.size() > 100){
      for (int i = 0; i < 100; i++){
        insertVariable(variableDataQueue.poll());
      }
    }
  }
}
