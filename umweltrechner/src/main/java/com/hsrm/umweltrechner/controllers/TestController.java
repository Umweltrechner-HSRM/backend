package com.hsrm.umweltrechner.controllers;

import javax.annotation.security.RolesAllowed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@Slf4j
@RequiredArgsConstructor
public class TestController {

  @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
  @RolesAllowed("admin")
  public ResponseEntity<String> hello(){
    return ResponseEntity.ok("hello");
  }
}
