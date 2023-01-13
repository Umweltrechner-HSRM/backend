package com.hsrm.umweltrechner.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsrm.umweltrechner.dao.mapper.FormulaMapper;
import com.hsrm.umweltrechner.dao.model.Formula;
import com.hsrm.umweltrechner.dto.DtoFormula;
import com.hsrm.umweltrechner.exceptions.NotFoundException;
import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;

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


  public void validateFormula(String formula) throws DivideByZeroException, DomainException,
      UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    formulaInterpreterService.checkSyntax(formula);
  }

  public Formula addFormula(DtoFormula formula) {
    Formula f = new Formula();
    f.setFormula(formula.getFormula());
    f.generateId();
    f.prepareInsert();
    formulaMapper.insert(f);
    formulaInterpreterService.init();
    return f;
  }

  public List<Formula> getAllFormulas() {
    return formulaMapper.selectAll();
  }

  public Formula updateFormula(String formulaId, DtoFormula formula) throws DivideByZeroException, DomainException, UnknownSymbolException, IllegalWriteException, IncorrectSyntaxException, OutOfRangeException {
    Formula oldFormula = formulaMapper.selectById(formulaId);
    if (oldFormula == null) {
      throw new NotFoundException("Formula not found");
    }
    validateFormula(formula.getFormula());
    oldFormula.setFormula(formula.getFormula());
    oldFormula.prepareUpdate();
    formulaMapper.update(oldFormula);
    formulaInterpreterService.init();
    return oldFormula;
  }
}
