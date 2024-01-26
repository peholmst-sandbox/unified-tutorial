package com.example.application.ui;

import com.vaadin.flow.component.Component;

import java.util.Collection;
import java.util.Collections;

public interface HasNavbarContent {

    default Collection<Component> getNavbarContent() {
        return Collections.emptyList();
    }
}
