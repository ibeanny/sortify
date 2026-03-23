package com.ibeanny.aisorter.service;

import com.ibeanny.aisorter.config.OpenAiConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class OpenAiService {

    private final OpenAiConfig config;
    private final HttpClient client;

    public OpenAiService(OpenAiConfig config) {
        this.config = config;
        this.client = HttpClient.newHttpClient();
    }

    public String testConnection() throws IOException, InterruptedException {
        String requestBody = """
        {
          "model": "gpt-4.1-mini",
          "input": "Say hello in one short sentence."
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public String processLines(List<String> lines) throws IOException, InterruptedException {
        String joinedLines = String.join("\n", lines);

        String prompt = """
        You are a smart document organizer.

        Your job is to read lines from a text file and group them into meaningful categories based on their content.

        The file may contain anything, such as legal records, medical notes, orders, personal information, business text, academic notes, or mixed structured text.

        Instructions:
        1. Read all lines carefully.
        2. Create short, useful, human-readable category names based on the actual content.
        3. Assign every line to the single best category.
        4. Reuse categories when multiple lines belong together.
        5. Do not create one category per line unless absolutely necessary.
        6. Keep category names concise and meaningful.
        7. Return ONLY valid JSON.
        8. Do not include markdown, code fences, explanations, or extra text.

        Return this exact JSON format:
        [
          {
            "category": "Category Name",
            "value": "Original line here"
          }
        ]

        Lines:
        %s
        """.formatted(joinedLines);

        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String requestBody = """
        {
          "model": "gpt-4.1-mini",
          "input": "%s"
        }
        """.formatted(escapedPrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}