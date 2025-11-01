# Spring Boot API with Java Client

This repository now contains a minimal Spring Boot REST API and a standalone Java client that consumes the API. The API exposes a static list of informative messages, while the client demonstrates how to call the endpoint and display the response.

## Project structure

- `spring-boot-api`: Spring Boot application that serves `/api/messages`.
- `java-client`: Java 17 console application that fetches the messages from the API.

## Prerequisites

- Java 17+
- Maven 3.9+

## Running the API

```bash
cd spring-boot-api
mvn spring-boot:run
```

This starts the API on `http://localhost:8080`.

## Running the client

In a separate terminal, run:

```bash
cd java-client
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer"
```

The client prints the messages returned by the API. You can also specify a custom base URL:

```bash
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" -Dexec.args="http://localhost:8080"
```

## Testing the API

```bash
cd spring-boot-api
mvn test
```
