package com.hsrm.umweltrechner.controllers;


import java.util.ArrayList;
import java.util.List;

import com.hsrm.umweltrechner.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.google.common.base.Preconditions;
import com.hsrm.umweltrechner.dto.DtoVariableData;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.services.FormulaInterpreterService;

import lombok.extern.slf4j.Slf4j;

@Controller
@EnableScheduling
@Slf4j
public class SensorControllerWS {

  @Autowired
  SimpMessagingTemplate template;

  @Autowired
  FormulaInterpreterService formulaInterpreterService;

  @Autowired
  HistoryService historyService;

  // Queue
  List<DtoVariableData> variableDataList = new ArrayList<>();

  @MessageMapping("/{sensor}")
  public void sendTemperature(@DestinationVariable("sensor") String sensor,
      DtoVariableData sensorData) throws OutOfRangeException, InvalidSymbolException {
    Preconditions.checkNotNull(sensorData.getTimestamp());
    Preconditions.checkNotNull(sensorData.getValue());

    if (!formulaInterpreterService.variableExists(sensor)) {
      throw new IllegalArgumentException("Sensor does not exist: " + sensor);
    }

    formulaInterpreterService.addSensorValue(sensor, sensorData.getValue(),
        sensorData.getTimestamp());
  }

  @Scheduled(fixedRateString = "${scheduler.rate}")
  public void scheduledVariables() {
    formulaInterpreterService.calculateAndGetVariables()
        .parallelStream()
        .forEach((x) -> {
          if (variableDataList.size() == 20){
            for (DtoVariableData variableData : variableDataList){
              historyService.insertVariable(variableData);
            }
            log.info("Added batch into history table, emptiying queue");
            variableDataList = new ArrayList<>();
          } else {
            variableDataList.add(x);
          }

          template.convertAndSend("/topic/" + x.getVariableName(), x);
          log.info("Sending variable: " + x);
        });
  }

}
