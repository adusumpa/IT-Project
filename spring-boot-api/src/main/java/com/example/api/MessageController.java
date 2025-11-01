package com.example.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final List<Message> messages = List.of(
            new Message(1, "Welcome", "This is a simple Spring Boot REST API."),
            new Message(2, "Integration", "It can be consumed from any Java client."),
            new Message(3, "Next steps", "Extend the API to meet your project requirements.")
    );

    @GetMapping
    public List<Message> getMessages() {
        return messages;
    }
}
