package com.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class MessageClient {

    private static final TypeReference<List<MessageDto>> LIST_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI baseUri;

    public MessageClient(String baseUrl) {
        this.baseUri = URI.create(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<MessageDto> listMessages() throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder("/api/messages")
                .GET()
                .build();
        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), LIST_TYPE);
    }

    public MessageDto getMessage(long id) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder("/api/messages/" + id)
                .GET()
                .build();
        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), MessageDto.class);
    }

    public MessageDto createMessage(String title, String content) throws IOException, InterruptedException {
        MessagePayload payload = new MessagePayload(title, content);
        String body = objectMapper.writeValueAsString(payload);
        HttpRequest request = baseRequestBuilder("/api/messages")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), MessageDto.class);
    }

    public MessageDto updateMessage(long id, String title, String content) throws IOException, InterruptedException {
        MessagePayload payload = new MessagePayload(title, content);
        String body = objectMapper.writeValueAsString(payload);
        HttpRequest request = baseRequestBuilder("/api/messages/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), MessageDto.class);
    }

    public void deleteMessage(long id) throws IOException, InterruptedException {
        HttpRequest request = baseRequestBuilder("/api/messages/" + id)
                .DELETE()
                .build();
        send(request);
    }

    private HttpRequest.Builder baseRequestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(baseUri.resolve(path))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json");
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return response;
        }
        throw buildException(response);
    }

    private IllegalStateException buildException(HttpResponse<String> response) {
        StringBuilder builder = new StringBuilder();
        builder.append("Request failed with status ")
                .append(response.statusCode());
        String body = response.body();
        if (body != null && !body.isBlank()) {
            builder.append(": ");
            try {
                Map<String, Object> payload = objectMapper.readValue(body, MAP_TYPE);
                Object title = payload.getOrDefault("title", "");
                Object detail = payload.getOrDefault("detail", body);
                builder.append(title).append(" - ").append(detail);
            } catch (JsonProcessingException ignored) {
                builder.append(body.trim());
            }
        }
        return new IllegalStateException(builder.toString());
    }

    private record MessagePayload(String title, String content) {
    }

    public record MessageDto(Long id, String title, String content, java.time.Instant createdAt, java.time.Instant updatedAt) {
    }
}
