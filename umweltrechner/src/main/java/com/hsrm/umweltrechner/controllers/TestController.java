package com.hsrm.umweltrechner.controllers;

import javax.annotation.security.RolesAllowed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
public class TestController {

  @GetMapping(value = "/secret", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<String> xsecret(){
    return ResponseEntity.ok("secret");
  }

  @GetMapping(value = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> xpublic(){
    return ResponseEntity.ok("public");
  }
}
