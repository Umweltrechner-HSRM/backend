package com.hsrm.umweltrechner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.hsrm.umweltrechner.dto.DtoSensorData;
import com.hsrm.umweltrechner.services.FormulaInterpreterService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Controller
@EnableScheduling
@Slf4j
public class SensorControllerWS {

  @Autowired
  SimpMessagingTemplate template;

  @Autowired
  FormulaInterpreterService formulaInterpreterService;


  @MessageMapping("/temperature/{sensor}")
  public void sendTemperature(@DestinationVariable("sensor") String sensor, DtoSensorData sensorData) {
    log.info("Received temperature data from sensor " + sensor + ": " + sensorData);
    formulaInterpreterService.addSensorValue(sensor, sensorData.getValue(), false);
  }

  @Scheduled(fixedRate = 1000)
  public void scheduledVariables(){
    formulaInterpreterService.calculateAndGetVariables().forEach((key, value) -> {
      log.info("Sending sensor data for sensor " + key + ": " + value);
      DtoSensorData sensorData = new DtoSensorData();
      sensorData.setValue(value.getValue());
      sensorData.setTimestamp(value.getLastModified());
      template.convertAndSend("/topic/" + key, sensorData);
    });
  }

}
