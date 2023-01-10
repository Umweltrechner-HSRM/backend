package com.hsrm.umweltrechner.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.hsrm.umweltrechner.dto.DtoVariableData;
import com.hsrm.umweltrechner.services.FormulaInterpreterService;
import com.hsrm.umweltrechner.syntax.exception.InvalidSymbolException;
import com.hsrm.umweltrechner.syntax.exception.OutOfRangeException;

import lombok.extern.slf4j.Slf4j;

@Controller
@EnableScheduling
@Slf4j
public class SensorControllerWS {

  @Autowired
  SimpMessagingTemplate template;

  @Autowired
  FormulaInterpreterService formulaInterpreterService;


  @MessageMapping("/{sensor}")
  public void sendTemperature(@DestinationVariable("sensor") String sensor,
      DtoVariableData sensorData) throws OutOfRangeException, InvalidSymbolException {
    if (!formulaInterpreterService.variableExists(sensor)) {
      log.info("Someone tried to send data to a non existing sensor: " + sensor);
      return;
    }

    formulaInterpreterService.addSensorValue(sensor, sensorData.getValue(),
        sensorData.getTimestamp());
  }

  @Scheduled(fixedRate = 1000)
  public void scheduledVariables() {
    formulaInterpreterService.calculateAndGetVariables().forEach((x) -> {
      template.convertAndSend("/topic/" + x.getVariableName(), x);
      log.info("Sending variable: " + x);
    });
  }

}
