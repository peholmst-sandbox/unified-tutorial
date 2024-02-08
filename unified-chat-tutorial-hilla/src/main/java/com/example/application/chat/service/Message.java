package com.example.application.chat.service;

import java.time.Instant;

public record Message(
        String messageId,
        String channelId,
        Instant timestamp,
        String author,
        String message) {
}
