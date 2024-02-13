package com.example.application.service;

import jakarta.annotation.Nullable;

import java.time.Instant;

public record ChatRoom(long id, String name, @Nullable Instant lastMessage) {
}
