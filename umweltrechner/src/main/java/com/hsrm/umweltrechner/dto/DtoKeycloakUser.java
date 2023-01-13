package com.hsrm.umweltrechner.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DtoKeycloakUser {

  private String id;

  private Date createdTimestamp;

  private String username;

  private boolean enabled;

  private boolean totp;

  private boolean emailVerified;

}
