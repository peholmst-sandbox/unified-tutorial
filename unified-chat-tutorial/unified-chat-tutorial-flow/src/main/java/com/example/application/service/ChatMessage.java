package com.example.application.service;

import java.time.Instant;

public record ChatMessage(long id, long roomId, Instant timestamp, String author, String message) {
}
