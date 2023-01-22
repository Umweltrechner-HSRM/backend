package com.hsrm.umweltrechner.controllers;

import java.util.List;

import com.hsrm.umweltrechner.dao.model.History;
import com.hsrm.umweltrechner.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/history")
public class HistoryController {

  @Autowired
  HistoryService historyService;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<History> getHistory(){
    return historyService.findAllHistoryData();
  }

  @GetMapping(path = "/getByName", produces = APPLICATION_JSON_VALUE)
  public List<History> getAllByName(
      @RequestParam String variableName
  ){
    return historyService.getAllByName(variableName);
  }
}
