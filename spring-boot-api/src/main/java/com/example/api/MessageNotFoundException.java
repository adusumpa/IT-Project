package com.example.api;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(long id) {
        super("Message " + id + " was not found");
    }
}
