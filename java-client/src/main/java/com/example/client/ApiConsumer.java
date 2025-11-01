package com.example.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ApiConsumer {

    public static void main(String[] args) throws IOException, InterruptedException {
        CliArguments cli = CliArguments.parse(args);
        if (cli.showHelp()) {
            printUsage();
            return;
        }

        MessageClient client = new MessageClient(cli.baseUrl());
        try {
            switch (cli.command()) {
                case "list" -> renderList(client);
                case "get" -> renderSingle(client, cli.requireLong("id"));
                case "create" -> renderSingle(
                        client.createMessage(cli.require("title"), cli.require("content")));
                case "update" -> renderSingle(
                        client.updateMessage(cli.requireLong("id"), cli.require("title"), cli.require("content")));
                case "delete" -> {
                    client.deleteMessage(cli.requireLong("id"));
                    System.out.println("Message deleted successfully");
                }
                default -> throw new IllegalArgumentException("Unknown command '" + cli.command() + "'");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.err.println(ex.getMessage());
            printUsage();
            System.exit(1);
        }
    }

    private static void renderList(MessageClient client) throws IOException, InterruptedException {
        List<MessageClient.MessageDto> messages = client.listMessages();
        System.out.printf("Retrieved %d messages:%n", messages.size());
        messages.forEach(ApiConsumer::printMessage);
    }

    private static void renderSingle(MessageClient client, long id) throws IOException, InterruptedException {
        MessageClient.MessageDto message = client.getMessage(id);
        printMessage(message);
    }

    private static void renderSingle(MessageClient.MessageDto message) {
        printMessage(message);
    }

    private static void printMessage(MessageClient.MessageDto message) {
        System.out.printf("[%d] %s%n", message.id(), message.title());
        System.out.printf("    %s%n", message.content());
        System.out.printf("    created: %s | updated: %s%n", message.createdAt(), message.updatedAt());
    }

    private static void printUsage() {
        System.out.println("Usage: mvn -q exec:java -Dexec.mainClass=\"com.example.client.ApiConsumer\" " +
                "-Dexec.args=\"[--base-url=http://localhost:8080] <command> [options]\"");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  list                                   List all messages");
        System.out.println("  get --id=<id>                          Retrieve a single message");
        System.out.println("  create --title=... --content=...       Create a message");
        System.out.println("  update --id=<id> --title=... --content=...  Update a message");
        System.out.println("  delete --id=<id>                       Delete a message");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --base-url=<url>   Override the API base URL (defaults to http://localhost:8080)");
    }

    private record CliArguments(String baseUrl, String command, Map<String, String> options, boolean showHelp) {
        static CliArguments parse(String[] args) {
            if (args.length == 0) {
                return new CliArguments(defaultBaseUrl(), "help", Map.of(), true);
            }

            Map<String, String> options = new HashMap<>();
            String baseUrl = defaultBaseUrl();
            String command = null;

            for (String arg : args) {
                if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h")) {
                    return new CliArguments(baseUrl, "help", options, true);
                }
                if (arg.startsWith("--base-url=")) {
                    baseUrl = arg.substring("--base-url=".length());
                    continue;
                }
                if (arg.startsWith("--")) {
                    int index = arg.indexOf('=');
                    if (index <= 2) {
                        throw new IllegalArgumentException("Invalid option: " + arg);
                    }
                    String key = arg.substring(2, index).toLowerCase(Locale.ROOT);
                    String value = arg.substring(index + 1);
                    options.put(key, value);
                } else if (command == null) {
                    command = arg.toLowerCase(Locale.ROOT);
                } else {
                    throw new IllegalArgumentException("Unexpected argument: " + arg);
                }
            }

            if (command == null) {
                return new CliArguments(baseUrl, "help", options, true);
            }

            return new CliArguments(baseUrl, command, options, false);
        }

        String baseUrl() {
            return baseUrl;
        }

        String command() {
            return command;
        }

        boolean showHelp() {
            return showHelp;
        }

        String require(String key) {
            return Optional.ofNullable(options.get(key))
                    .filter(value -> !value.isBlank())
                    .orElseThrow(() -> new IllegalArgumentException("Missing --" + key + " option"));
        }

        long requireLong(String key) {
            String raw = require(key);
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Option --" + key + " must be a number");
            }
        }

        private static String defaultBaseUrl() {
            return "http://localhost:8080";
        }
    }
}
