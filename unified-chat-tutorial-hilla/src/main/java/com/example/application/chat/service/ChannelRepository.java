package com.example.application.chat.service;

import com.example.application.annotation.SPI;

import java.util.List;
import java.util.Optional;

@SPI
public interface ChannelRepository {

    String generateId();

    List<Channel> findAll();

    void save(Channel channel);

    Optional<Channel> findById(String channelId);

    boolean exists(String channelId);
}
