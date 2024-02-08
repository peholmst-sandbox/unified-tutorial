package com.example.application.chat.service;

import java.util.List;

public interface MessageRepository {
    String generateId(String channelId);

    List<Message> findLatest(String channelId, int fetchMax);

    void save(Message message);
}
