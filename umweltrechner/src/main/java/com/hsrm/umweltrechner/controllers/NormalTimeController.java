package com.hsrm.umweltrechner.controllers;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@EnableScheduling
public class NormalTimeController {

  @Data
  public static class TemperatureDto {
    private Integer temperature;
    private ZonedDateTime time;
  }

  private final ConcurrentLinkedQueue<TemperatureDto> queue = new ConcurrentLinkedQueue<>();

  @CrossOrigin
  @GetMapping("/temperature")
  public TemperatureDto getLatestTime() {
    return queue.peek();
  }

  @Scheduled(fixedRate = 1000)
  public void addTime() {
    queue.poll();
    TemperatureDto temperatureDto = new TemperatureDto();
    temperatureDto.setTime(ZonedDateTime.now());
    temperatureDto.setTemperature((int) (Math.random() * 100));
    queue.add(temperatureDto);
  }



}
