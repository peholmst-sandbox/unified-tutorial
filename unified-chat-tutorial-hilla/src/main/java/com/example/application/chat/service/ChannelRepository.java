package com.example.application.chat.service;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository {

    String generateId();

    List<Channel> findAll();

    void save(Channel channel);

    Optional<Channel> findById(String channelId);

    boolean exists(String channelId);
}
