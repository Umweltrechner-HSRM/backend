package com.hsrm.umweltrechner.dto;

import java.util.List;

import com.hsrm.umweltrechner.dao.model.Variable;
import lombok.Data;

@Data
public class DtoVariable {

  private String name;

  private Double minThreshold;

  private Double maxThreshold;
}
