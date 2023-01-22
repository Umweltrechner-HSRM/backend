package com.hsrm.umweltrechner.services;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.hsrm.umweltrechner.dao.mapper.HistoryMapper;
import com.hsrm.umweltrechner.dao.model.History;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
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

  @Scheduled(fixedRateString = "5000")
  public void insertAllVariablesInQueue(){
    if (variableDataQueue.size() > 100){
      for (int i = 0; i < 100; i++){
        insertVariable(variableDataQueue.poll());
      }
      log.info("elements initialized");
    }
    log.info("queue size to small");
  }

  public List<History> findAllHistoryData(){
    return historyMapper.selectAll();
  }

  public List<History> getAllByName(String variableName){
    return historyMapper.selectAllByName(variableName);
  }

}
