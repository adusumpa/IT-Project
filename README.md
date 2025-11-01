# Spring Boot API with Advanced Java Client

This repository contains a Spring Boot REST API backed by an in-memory H2 database and a Java 17 console
client that exercises the API. The API provides CRUD endpoints for managing "messages" that you can adapt to
showcase interview-style projects. The client demonstrates how to call the API with the modern `HttpClient`
from Java 17, parse JSON payloads with Jackson, and provide a small CLI experience.

## Project structure

- `spring-boot-api`: Spring Boot application that serves `/api/messages` and persists data with Spring Data JPA.
- `java-client`: Java 17 console application that lets you list, create, update, and delete messages from the API.

## Prerequisites

- Java 17+
- Maven 3.9+

## Running the API

```bash
cd spring-boot-api
mvn spring-boot:run
```

This starts the API on `http://localhost:8080` with sample data loaded into an in-memory H2 database. You can
inspect the data via the H2 console at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:messages`).

### Available endpoints

| Method | Path                | Description                        |
|--------|---------------------|------------------------------------|
| GET    | `/api/messages`     | List all messages                  |
| GET    | `/api/messages/{id}`| Retrieve a single message          |
| POST   | `/api/messages`     | Create a message                   |
| PUT    | `/api/messages/{id}`| Update an existing message         |
| DELETE | `/api/messages/{id}`| Delete a message                   |

Request bodies accept `title` and `content` fields. Validation errors return [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807)
problem details with helpful messages.

## Running the Java client

Open a new terminal while the API is running and execute commands through Maven:

```bash
cd java-client
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" -Dexec.args="list"
```

You can supply a custom base URL and invoke CRUD operations:

```bash
# List all messages
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" \
    -Dexec.args="--base-url=http://localhost:8080 list"

# Create a message (quotes keep arguments with spaces intact)
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" \
    -Dexec.args="create --title='System Design' --content='Prepare a crisp elevator pitch.'"

# Update and delete
mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" \
    -Dexec.args="update --id=1 --title='Updated' --content='Refine talking points.'"

mvn -q exec:java -Dexec.mainClass="com.example.client.ApiConsumer" \
    -Dexec.args="delete --id=1"
```

Run without arguments (or with `--help`) to see usage instructions.

## Testing the API

```bash
cd spring-boot-api
mvn test
```

Integration tests cover listing data, a full CRUD lifecycle, and validation failures using MockMvc.
