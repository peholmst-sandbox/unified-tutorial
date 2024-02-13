package com.example.application.service;

public class NoSuchChatRoomException extends IllegalArgumentException {

    public NoSuchChatRoomException() {
        super("The specified chat room does not exist");
    }
}
