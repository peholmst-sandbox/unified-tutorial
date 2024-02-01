package com.example.application.ui;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUser {

    private final AuthenticationContext authenticationContext;

    public CurrentUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void logout() {
        authenticationContext.logout();
    }

    public String getName() {
        return authenticationContext.getPrincipalName().orElse("");
    }

    public boolean hasRole(String role) {
        return authenticationContext
                .getAuthenticatedUser(UserDetails.class)
                .filter(ud -> ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)))
                .isPresent();
    }
}
