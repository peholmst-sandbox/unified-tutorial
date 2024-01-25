package com.example.application.domain.model;

import com.example.application.domain.base.BaseEntity;
import jakarta.persistence.Entity;

@Entity
public class Industry extends BaseEntity {

    public static final String PROP_NAME = "name";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
