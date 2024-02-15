package com.example.application.app.chat;

public class InvalidChannelException extends IllegalArgumentException {

    public InvalidChannelException() {
        super("The specified channel does not exist");
    }
}
