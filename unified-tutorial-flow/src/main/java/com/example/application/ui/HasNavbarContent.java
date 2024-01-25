package com.example.application.ui;

import com.vaadin.flow.component.Component;

import java.util.Optional;

public interface HasNavbarContent {

    default Optional<Component> getNavbarContent() {
        return Optional.empty();
    }
}
