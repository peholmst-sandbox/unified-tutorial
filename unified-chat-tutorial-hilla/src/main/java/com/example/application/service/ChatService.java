package com.example.application.service;

import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    List<ChatRoom> rooms();

    void createRoom(String name);

    String roomName(long roomId) throws NoSuchChatRoomException;

    Flux<ChatMessage> liveMessages(long roomId) throws NoSuchChatRoomException;

    List<ChatMessage> messageHistory(long roomId, int fetchMax) throws NoSuchChatRoomException;

    void postMessage(long roomId, String message) throws NoSuchChatRoomException;
}
