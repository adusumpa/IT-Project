package com.example.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must be at most 120 characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 2048, message = "Content must be at most 2048 characters")
        String content
) {
}
