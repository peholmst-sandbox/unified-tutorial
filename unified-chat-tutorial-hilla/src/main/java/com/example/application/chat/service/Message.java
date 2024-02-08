package com.example.application.chat.service;

import com.example.application.annotation.API;

import java.time.Instant;

@API
public record Message(
        String messageId,
        String channelId,
        Instant timestamp,
        String author,
        String message) {
}
