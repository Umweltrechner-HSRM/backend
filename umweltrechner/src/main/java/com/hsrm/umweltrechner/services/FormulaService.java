package com.hsrm.umweltrechner.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.syntax.exception.DivideByZeroException;
import com.hsrm.umweltrechner.syntax.exception.DomainException;
import com.hsrm.umweltrechner.syntax.exception.IllegalWriteException;
import com.hsrm.umweltrechner.syntax.exception.IncorrectSyntaxException;
import com.hsrm.umweltrechner.syntax.exception.OutOfRangeException;
import com.hsrm.umweltrechner.syntax.exception.UnknownSymbolException;

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


  public void validateFormula(String formula) throws DivideByZeroException, DomainException, UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    formulaInterpreterService.checkSyntax(formula);
  }

  public void addFormula(DtoFormula formula) {
    Formula f = new Formula();
    f.setFormula(formula.getFormula());
    f.generateId();
    f.prepareInsert();
    formulaMapper.insert(f);
    formulaInterpreterService.init();
  }

  public List<Formula> getAllFormulas() {
    return formulaMapper.selectAll();
  }
}
