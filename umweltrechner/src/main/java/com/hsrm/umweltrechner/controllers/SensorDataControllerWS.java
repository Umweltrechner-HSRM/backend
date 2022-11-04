package com.hsrm.umweltrechner.controllers;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import lombok.Data;

@Controller
@EnableScheduling
public class SensorDataControllerWS {

  @Autowired
  SimpMessagingTemplate template;

  @Data
  public static class SensorData {
    private String value;
    private String unit;
    private ZonedDateTime timestamp;
  }


  @Scheduled(fixedRate = 1000)
  public void sendAdhocMessages() {
    SensorData sensorData = new SensorData();
    sensorData.setValue((int) (Math.random() * 100) + "");
    sensorData.setUnit("C");
    sensorData.setTimestamp(ZonedDateTime.now());
    template.convertAndSend("/topic/test", sensorData);
  }

  @MessageMapping("/temperature")
  @SendTo("/topic/temperature")
  public SensorData sendTemperature(SensorData sensorData) {
    return sensorData;
  }


}
