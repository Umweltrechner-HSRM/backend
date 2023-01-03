package com.hsrm.umweltrechner.util;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentSession {

  public static String getCurrentUserName() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    KeycloakPrincipal principal = (KeycloakPrincipal)auth.getPrincipal();
    KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
    AccessToken accessToken = session.getToken();
    return accessToken.getPreferredUsername();
  }

}
