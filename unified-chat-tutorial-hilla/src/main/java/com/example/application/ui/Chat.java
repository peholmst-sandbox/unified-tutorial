package com.example.application.ui;

import com.example.application.service.ChatMessage;
import com.example.application.service.ChatRoom;
import com.example.application.service.ChatService;
import dev.hilla.BrowserCallable;
import dev.hilla.Nonnull;
import jakarta.annotation.security.PermitAll;
import reactor.core.publisher.Flux;

import java.util.List;

@BrowserCallable
@PermitAll
public class Chat {

    // TODO Could we refactor the ChatService so that that could be the browser callable service?

    private final ChatService chatService;

    public Chat(ChatService chatService) {
        this.chatService = chatService;
    }

    public @Nonnull List<@Nonnull ChatRoom> rooms() {
        return chatService.rooms();
    }

    public void createRoom(@Nonnull String name) {
        chatService.createRoom(name);
    }

    public @Nonnull String roomName(long roomId) {
        return chatService.roomName(roomId);
    }

    public @Nonnull Flux<@Nonnull ChatMessage> liveMessages(long roomId) {
        return chatService.liveMessages(roomId);
    }

    public @Nonnull List<@Nonnull ChatMessage> messageHistory(long roomId, int fetchMax) {
        return chatService.messageHistory(roomId, fetchMax);
    }

    public void postMessage(long roomId, @Nonnull String message) {
        chatService.postMessage(roomId, message);
    }
}
