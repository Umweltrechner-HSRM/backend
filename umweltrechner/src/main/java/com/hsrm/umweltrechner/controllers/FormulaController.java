package com.hsrm.umweltrechner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.services.FormulaService;
import com.hsrm.umweltrechner.syntax.exception.DivideByZeroException;
import com.hsrm.umweltrechner.syntax.exception.DomainException;
import com.hsrm.umweltrechner.syntax.exception.IllegalWriteException;
import com.hsrm.umweltrechner.syntax.exception.IncorrectSyntaxException;
import com.hsrm.umweltrechner.syntax.exception.OutOfRangeException;
import com.hsrm.umweltrechner.syntax.exception.UnknownSymbolException;

@RestController
@RequestMapping("/formula")
public class FormulaController {

  @Autowired
  private FormulaService formulaService;

  @PostMapping("/validate")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<Void> validateFormula(@RequestBody DtoFormula formula) {
    try {
      formulaService.validateFormula(formula.getFormula());
      return ResponseEntity.ok().build();
    } catch (DivideByZeroException | DomainException |
             UnknownSymbolException | IllegalWriteException | IncorrectSyntaxException |
             OutOfRangeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/add")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<Void> addFormula(@RequestBody DtoFormula formula) {
    formulaService.addFormula(formula);
    return ResponseEntity.ok().build();
  }


}
