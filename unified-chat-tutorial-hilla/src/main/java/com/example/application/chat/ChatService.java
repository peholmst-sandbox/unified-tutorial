package com.example.application.chat;

import com.example.application.security.Roles;
import dev.hilla.BrowserCallable;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@BrowserCallable
@RolesAllowed(Roles.USER)
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final Duration BUFFER_DURATION = Duration.ofMillis(500);
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final Clock clock;
    private final Sinks.Many<Message> sink = Sinks.many().multicast().directBestEffort();

    public ChatService(ChannelRepository channelRepository, MessageRepository messageRepository, Clock clock) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    public List<Channel> channels() {
        return channelRepository.findAll();
    }

    @RolesAllowed(Roles.ADMIN)
    public Channel createChannel(String name) {
        var channel = new Channel(channelRepository.generateId(), name, null);
        channelRepository.save(channel);
        return channel;
    }

    public Optional<Channel> channel(String channelId) {
        return channelRepository.findById(channelId);
    }

    public Flux<List<Message>> liveMessages(String channelId) throws InvalidChannelException {
        if (!channelRepository.exists(channelId)) {
            throw new InvalidChannelException();
        }
        return sink.asFlux().filter(m -> m.channelId().equals(channelId)).buffer(BUFFER_DURATION);
    }

    public List<Message> messageHistory(String channelId, int fetchMax) {
        return messageRepository.findLatest(channelId, fetchMax);
    }

    public void postMessage(String channelId, String message) throws InvalidChannelException {
        if (!channelRepository.exists(channelId)) {
            throw new InvalidChannelException();
        }
        var author = SecurityContextHolder.getContext().getAuthentication().getName();
        var msg = new Message(messageRepository.generateId(channelId), channelId, clock.instant(), author, message);
        messageRepository.save(msg);
        var result = sink.tryEmitNext(msg);
        if (result.isFailure()) {
            log.error("Error posting message to channel {}: {}", channelId, result);
        }
    }
}