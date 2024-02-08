package com.example.application.chat;

import java.util.List;

public interface MessageRepository {
    String generateId(String channelId);

    List<Message> findLatest(String channelId, int fetchMax);

    void save(Message message);
}
