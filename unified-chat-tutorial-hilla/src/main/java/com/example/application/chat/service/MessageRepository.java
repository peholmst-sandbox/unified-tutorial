package com.example.application.chat.service;

import com.example.application.annotation.SPI;

import java.util.List;

@SPI
public interface MessageRepository {
    String generateId(String channelId);

    List<Message> findLatest(String channelId, int fetchMax);

    void save(Message message);
}
