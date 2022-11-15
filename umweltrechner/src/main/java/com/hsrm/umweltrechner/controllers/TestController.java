package com.hsrm.umweltrechner.controllers;


import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
public class TestController {

  @Data
  public static class Test {
    private String value;
  }

  @GetMapping(value = "/secret", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<Test> xsecret(){
    Test test = new Test();
    test.setValue("secret");
    return ResponseEntity.ok(test);
  }

  @GetMapping(value = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> xpublic(){
    return ResponseEntity.ok("public");
  }
}
