package com.example.application.chat;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository {

    List<Channel> findAll();

    Channel save(NewChannel newChannel);

    Optional<Channel> findById(String channelId);

    boolean exists(String channelId);
}
