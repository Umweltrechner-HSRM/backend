package com.hsrm.umweltrechner.controllers;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import lombok.Data;

@Controller
@EnableScheduling
public class WSController {

  @Autowired
  SimpMessagingTemplate template;

  @Data
  public static class TemperatureDataDto {
    private Integer temperature;
    private ZonedDateTime timestamp;
  }


  @Scheduled(fixedRate = 1000)
  public void sendAdhocMessages() {
    TemperatureDataDto temperatureDataDto = new TemperatureDataDto();
    temperatureDataDto.setTemperature((int) (Math.random() * 100));
    temperatureDataDto.setTimestamp(ZonedDateTime.now());
    template.convertAndSend("/topic/temperature", temperatureDataDto);
  }
}
