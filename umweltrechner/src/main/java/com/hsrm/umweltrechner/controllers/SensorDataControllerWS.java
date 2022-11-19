package com.hsrm.umweltrechner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.hsrm.umweltrechner.services.FormulaInterpreterService;

import lombok.Data;

@Controller
@EnableScheduling
public class SensorDataControllerWS {

  @Autowired
  SimpMessagingTemplate template;

  @Autowired
  FormulaInterpreterService formulaInterpreterService;

  @Data
  public static class SensorData {
    private Double value;
    private Long timestamp;
  }


  @MessageMapping("/temperature/{sensor}")
  public void sendTemperature(SensorData sensorData) {
    System.out.println("Received: " + sensorData);
  }

  @Scheduled(fixedRate = 1000)
  public void scheduledVariables(){
    formulaInterpreterService.getVariables().forEach((key, value) -> {
      SensorData sensorData = new SensorData();
      sensorData.setValue(value.getValue());
      sensorData.setTimestamp(value.getLastModified());
      System.out.println("Sending " + key + " with value " + value.getValue());
      template.convertAndSend("/topic/" + key, sensorData);
    });
  }


}
