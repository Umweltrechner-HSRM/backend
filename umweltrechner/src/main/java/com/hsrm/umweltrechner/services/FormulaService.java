package com.hsrm.umweltrechner.services;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.syntax.FormelInterpreter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FormulaService {

  private final FormulaMapper formulaMapper;

  private final FormulaInterpreterService formulaInterpreterService;


  @Autowired
  public FormulaService(FormulaMapper formulaMapper,
      FormulaInterpreterService formulaInterpreterService) {
    this.formulaMapper = formulaMapper;
    this.formulaInterpreterService = formulaInterpreterService;
  }


  public void validateFormula(String formula) throws FormelInterpreter.IllegalWriteException, FormelInterpreter.UnknownVariableException, FormelInterpreter.IncorrectSyntaxException {
    formulaInterpreterService.checkSyntax(formula);
  }

  public void addFormula(DtoFormula formula) {
    Formula f = new Formula();
    f.setFormula(formula.getFormula());
    f.setId(UUID.randomUUID().toString());
    formulaMapper.insert(f);
    formulaInterpreterService.addFormula();
  }
}
