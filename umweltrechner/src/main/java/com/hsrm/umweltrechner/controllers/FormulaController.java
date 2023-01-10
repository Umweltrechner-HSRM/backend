package com.hsrm.umweltrechner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.services.FormulaService;

@RestController
@RequestMapping("/formula")
public class FormulaController {

  @Autowired
  private FormulaService formulaService;

  @PostMapping("/validate")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<String> validateFormula(@RequestBody DtoFormula formula) {
    try {
      formulaService.validateFormula(formula.getFormula());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping("/add")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<Void> addFormula(@RequestBody DtoFormula formula) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(formula.getFormula()));
    formulaService.addFormula(formula);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public List<Formula> getFormulas() {
    return formulaService.getAllFormulas();
  }

}
