package com.example.api;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<MessageResponse> getMessages() {
        return service.getAllMessages().stream()
                .map(MessageResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public MessageResponse getMessage(@PathVariable long id) {
        return MessageResponse.fromEntity(service.getMessage(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createMessage(@Valid @RequestBody MessageRequest request) {
        Message created = service.createMessage(request.title(), request.content());
        MessageResponse body = MessageResponse.fromEntity(created);
        return ResponseEntity.created(URI.create("/api/messages/" + created.getId()))
                .body(body);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse updateMessage(@PathVariable long id, @Valid @RequestBody MessageRequest request) {
        return MessageResponse.fromEntity(service.updateMessage(id, request.title(), request.content()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable long id) {
        service.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
