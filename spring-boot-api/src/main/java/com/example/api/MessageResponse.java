package com.example.api;

import java.time.Instant;

public record MessageResponse(
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt
) {

    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getTitle(),
                message.getContent(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
