package com.hsrm.umweltrechner.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TimeController {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  @PostConstruct
  public void init() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      executor.shutdown();
      try {
        executor.awaitTermination(1, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error(e.toString());
      }
    }));
  }

  @GetMapping("/time")
  @CrossOrigin
  public SseEmitter streamDateTime() {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

    sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
    sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
    sseEmitter.onError((ex) -> log.info("SseEmitter got error:", ex));

    executor.execute(() -> {
      for (int i = 0; i < 15; i++) {
        try {
          sseEmitter.send(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy " +
              "hh:mm:ss")));
          sleep(1, sseEmitter);
        } catch (IOException e) {
          e.printStackTrace();
          sseEmitter.completeWithError(e);
        }
      }
      sseEmitter.complete();
    });

    log.info("Controller exits");
    return sseEmitter;
  }

  private void sleep(int seconds, SseEmitter sseEmitter) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
      sseEmitter.completeWithError(e);
    }
  }
}