package com.hsrm.umweltrechner.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;
import com.hsrm.umweltrechner.services.FormulaService;

@RestController
@RequestMapping("/formula")
public class FormulaController {

  @Autowired
  private FormulaService formulaService;

  @PostMapping(path = "/validate", consumes = APPLICATION_JSON_VALUE, produces =
      APPLICATION_JSON_VALUE)
  public ResponseEntity<String> validateFormula(@RequestBody DtoFormula formula) throws DivideByZeroException, DomainException, UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    formulaService.validateFormula(formula.getFormula());
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = "/add", consumes = APPLICATION_JSON_VALUE, produces =
      APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public Formula addFormula(@RequestBody DtoFormula formula) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(formula.getFormula()));
    formulaService.validateFormula(formula.getFormula());
    return formulaService.addFormula(formula);
  }

  @PutMapping(path="/{id}", consumes = APPLICATION_JSON_VALUE, produces =
      APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public Formula updateFormula(@PathVariable("id") String id,
      @RequestBody DtoFormula formula) throws DivideByZeroException, DomainException,
      UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(formula.getFormula()));
    return formulaService.updateFormula(id, formula);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<String> deleteFormula(@PathVariable("id") String id) {
    formulaService.deleteFormula(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<Formula> getFormulas() {
    return formulaService.getAllFormulas();
  }

}
