package com.hsrm.umweltrechner.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsrm.umweltrechner.access.KeycloakClient;
import com.hsrm.umweltrechner.dto.DtoKeycloakUser;

@RestController
@RequestMapping("/users")
public class CustomerController {

  @Autowired
  private KeycloakClient keycloakClient;

  @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('admin')")
  public List<DtoKeycloakUser> getUsers() {
    return keycloakClient.getAllUsers();
  }


}
