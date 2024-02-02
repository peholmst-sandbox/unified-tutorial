package com.example.application.ui;

import com.vaadin.flow.spring.security.AuthenticationContext;
import dev.hilla.BrowserCallable;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@BrowserCallable
@PermitAll
public class CurrentUser {

    private final AuthenticationContext authenticationContext;

    public CurrentUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public Optional<User> getDetails() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(ud -> new User(
                        ud.getUsername(),
                        ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new)
                ));
    }

}
