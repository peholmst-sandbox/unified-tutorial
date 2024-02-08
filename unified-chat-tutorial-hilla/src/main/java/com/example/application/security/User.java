package com.example.application.security;

import com.example.application.annotation.API;

@API
public record User(String name, String[] roles) {
}
