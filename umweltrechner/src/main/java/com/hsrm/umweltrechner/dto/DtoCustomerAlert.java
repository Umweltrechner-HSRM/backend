package com.hsrm.umweltrechner.dto;


import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class DtoCustomerAlert {

  private String id;

  private String variableName;

  private String phoneNumber;

  private String email;

  private ZonedDateTime lastNotified;

}
