package com.example.application.chat.inmemory;

import com.example.application.chat.Message;
import com.example.application.chat.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
class InMemoryMessageRepository implements MessageRepository {

    private final ConcurrentMap<String, MessageArchive> messageArchives = new ConcurrentHashMap<>();

    @Override
    public String generateId(String channelId) {
        return messageArchives.computeIfAbsent(channelId, MessageArchive::new).generateId();
    }

    @Override
    public List<Message> findLatest(String channelId, int fetchMax) {
        return Optional.ofNullable(messageArchives.get(channelId))
                .map(archive -> archive.findLatest(fetchMax))
                .orElse(Collections.emptyList());
    }

    @Override
    public void save(Message message) {
        messageArchives.computeIfAbsent(message.channelId(), MessageArchive::new).save(message);
    }

    private static class MessageArchive {
        private final AtomicLong nextMessageId = new AtomicLong(1);
        private final List<Message> messages = new ArrayList<>();
        private final String channelId;

        private MessageArchive(String channelId) {
            this.channelId = channelId;
        }

        public String generateId() {
            return "%s-%d".formatted(channelId, nextMessageId.getAndIncrement());
        }

        public List<Message> findLatest(int fetchMax) {
            synchronized (this) {
                return messages
                        .stream()
                        .sorted(Comparator.comparing(Message::timestamp))
                        .skip(Math.max(0, messages.size() - fetchMax))
                        .toList();
            }
        }

        public void save(Message message) {
            synchronized (this) {
                messages.add(message);
            }
        }
    }
}
