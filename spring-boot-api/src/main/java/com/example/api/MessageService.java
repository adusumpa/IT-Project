package com.example.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> getAllMessages() {
        return repository.findAllByOrderByCreatedAtAsc();
    }

    public Message getMessage(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }

    @Transactional
    public Message createMessage(String title, String content) {
        Message message = new Message(title, content);
        return repository.save(message);
    }

    @Transactional
    public Message updateMessage(long id, String title, String content) {
        Message existing = getMessage(id);
        existing.setTitle(title);
        existing.setContent(content);
        return repository.save(existing);
    }

    @Transactional
    public void deleteMessage(long id) {
        Message existing = getMessage(id);
        repository.delete(existing);
    }
}
