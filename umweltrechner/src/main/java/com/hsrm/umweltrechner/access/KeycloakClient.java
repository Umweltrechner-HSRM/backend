package com.hsrm.umweltrechner.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.hsrm.umweltrechner.dto.DtoKeycloakUser;

@Service
public class KeycloakClient {

  // reference
  // https://www.keycloak.org/docs-api/15.0/rest-api/index.html#_users_resource
  @Autowired
  private KeycloakRestTemplate keycloakRestTemplate;

  @Value("${keycloak.auth-server-url}")
  private String keycloakUrl;

  @Value("${keycloak.realm}")
  private String realm;

  public List<DtoKeycloakUser> getAllUsers() {
    String url =
        UriComponentsBuilder.fromHttpUrl(keycloakUrl)
            .path("/admin/realms/" + realm)
            .path("/users")
            .toUriString();

    ResponseEntity<DtoKeycloakUser[]> response = keycloakRestTemplate.getForEntity(
        url, DtoKeycloakUser[].class);

    if (response.getStatusCode().isError() || response.getBody() == null) {
      throw new RuntimeException("Error while getting users from keycloak");
    }

    return new ArrayList<>(Arrays.asList(response.getBody()));
  }

  public DtoKeycloakUser getUserById(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(keycloakUrl)
            .path("/admin/realms/" + realm)
            .path("/users")
            .path(id)
            .toUriString();
    ResponseEntity<DtoKeycloakUser> response = keycloakRestTemplate.getForEntity(
        url, DtoKeycloakUser.class);
    if (response.getStatusCode().isError() || response.getBody() == null) {
      throw new RuntimeException("Error while getting user from keycloak");
    }
    return response.getBody();
  }

}
