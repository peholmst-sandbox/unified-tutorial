package com.example.application.chat.service;

import com.example.application.annotation.API;
import jakarta.annotation.Nullable;

@API
public record Channel(
        String id,
        String name,
        @Nullable Message lastMessage
) {

    public Channel(String id, String name) {
        this(id, name, null);
    }
}
