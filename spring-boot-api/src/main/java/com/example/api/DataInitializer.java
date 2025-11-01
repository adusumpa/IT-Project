package com.example.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MessageRepository repository;

    public DataInitializer(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<Message> messages = List.of(
                new Message("Welcome to the portfolio", "This API powers interview-style examples you can extend."),
                new Message("Share your achievements", "Use this project to showcase clean architecture and testing."),
                new Message("Iterate quickly", "Add authentication, persistence, or messaging to evolve the system.")
        );
        repository.saveAll(messages);
    }
}
