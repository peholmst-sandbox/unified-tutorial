package com.example.application.service;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
class InMemoryChatService implements ChatService {

    private final AtomicLong nextRoomId = new AtomicLong();
    private final ConcurrentMap<Long, ChatRoomController> chatRooms = new ConcurrentHashMap<>();

    InMemoryChatService() {
        createRoom("My first room");
        createRoom("My second room");
    }

    @Override
    public List<ChatRoom> rooms() {
        return chatRooms.values().stream().map(c -> new ChatRoom(c.roomId(), c.name(), c.lastMessageTimestamp())).toList();
    }

    @Override
    public void createRoom(String name) {
        var roomId = nextRoomId.getAndIncrement();
        var controller = new ChatRoomController(roomId, name, Clock.systemUTC());
        chatRooms.put(roomId, controller);
    }

    private ChatRoomController getChatRoom(long roomId) {
        return Optional.ofNullable(chatRooms.get(roomId)).orElseThrow(NoSuchChatRoomException::new);
    }

    @Override
    public String roomName(long roomId) {
        return getChatRoom(roomId).name();
    }

    @Override
    public Flux<ChatMessage> liveMessages(long roomId) {
        return getChatRoom(roomId).hotMessageStream();
    }

    @Override
    public List<ChatMessage> messageHistory(long roomId, int fetchMax) {
        return getChatRoom(roomId).messageHistory(fetchMax);
    }

    @Override
    public void postMessage(long roomId, String message) {
        var author = "Joe Cool"; // TODO Fetch from SecurityContext
        getChatRoom(roomId).post(author, message);
    }

    private static class ChatRoomController {

        private static final Logger log = LoggerFactory.getLogger(ChatRoomController.class);

        private final long roomId;
        private final String name;
        private final Clock clock;

        private final AtomicLong nextMessageId = new AtomicLong();
        private final List<ChatMessage> archive = new ArrayList<>();
        private final Sinks.Many<ChatMessage> sink;

        private ChatRoomController(long roomId, String name, Clock clock) {
            this.roomId = roomId;
            this.name = name;
            this.clock = clock;
            sink = Sinks.many().multicast().directBestEffort();
        }

        public void post(String author, String message) {
            var msg = new ChatMessage(nextMessageId.getAndIncrement(), roomId, clock.instant(), author, message);
            var result = sink.tryEmitNext(msg);
            if (result.isFailure()) {
                log.error("Error posting message to chat room {}: {}", roomId, result);
            }
            synchronized (archive) {
                archive.add(msg);
            }
        }

        public long roomId() {
            return roomId;
        }

        public String name() {
            return name;
        }

        public @Nullable Instant lastMessageTimestamp() {
            try {
                synchronized (archive) {
                    return archive.getLast().timestamp();
                }
            } catch (NoSuchElementException ex) {
                return null;
            }
        }

        public Flux<ChatMessage> hotMessageStream() {
            return sink.asFlux();
        }

        public List<ChatMessage> messageHistory(int fetchMax) {
            synchronized (archive) {
                return List.copyOf(archive.subList(Math.max(0, archive.size() - fetchMax), archive.size()));
            }
        }
    }
}
