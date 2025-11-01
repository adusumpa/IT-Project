package com.example.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ApiConsumer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException, InterruptedException {
        String baseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/messages"))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Unexpected response status: " + response.statusCode());
        }

        List<Message> messages = OBJECT_MAPPER.readValue(response.body(), new TypeReference<>() {});
        messages.forEach(message -> System.out.printf("%d - %s: %s%n", message.id(), message.title(), message.content()));
    }

    public record Message(long id, String title, String content) {
    }
}
