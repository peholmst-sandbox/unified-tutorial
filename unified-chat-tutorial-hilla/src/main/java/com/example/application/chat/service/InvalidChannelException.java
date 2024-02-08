package com.example.application.chat.service;

import com.example.application.annotation.API;

@API
public class InvalidChannelException extends IllegalArgumentException {

    public InvalidChannelException() {
        super("The specified channel does not exist");
    }
}
