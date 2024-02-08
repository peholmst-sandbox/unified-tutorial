package com.example.application.security;

import com.example.application.annotation.API;
import dev.hilla.BrowserCallable;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@BrowserCallable
@PermitAll
@API
public class CurrentUser {

    public Optional<User> get() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> !(auth instanceof AnonymousAuthenticationToken))
                .map(auth -> new User(
                        auth.getName(),
                        auth.getAuthorities().stream().filter(Roles::isRole).map(Roles::toRoleName).toArray(String[]::new)
                ));
    }
}
