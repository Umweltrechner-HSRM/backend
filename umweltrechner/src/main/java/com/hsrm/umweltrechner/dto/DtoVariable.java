package com.hsrm.umweltrechner.dto;

import java.util.List;

import com.hsrm.umweltrechner.dao.model.Variable;
import lombok.Data;

@Data
public class DtoVariable {

  private List<Variable> variables;

  private String mail;

  private String mobile;
}
