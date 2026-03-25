package com.ibeanny.aisorter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeanny.aisorter.exception.OpenAiIntegrationException;
import com.ibeanny.aisorter.model.ClassifiedLine;
import com.ibeanny.aisorter.model.CombinedCategoryGroup;
import com.ibeanny.aisorter.model.ProcessResponse;
import com.ibeanny.aisorter.model.ProcessedFileResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DocumentProcessingService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DocumentProcessingService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public ProcessResponse processFiles(MultipartFile[] files) throws IOException, InterruptedException {
        List<ProcessedFileResult> processedFiles = new ArrayList<>();
        int totalLines = 0;

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (fileName == null || !fileName.toLowerCase().endsWith(".txt")) {
                throw new IllegalArgumentException("Only .txt files are allowed.");
            }

            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<String> cleanedLines = extractCleanLines(content);
            if (cleanedLines.isEmpty()) {
                processedFiles.add(new ProcessedFileResult(fileName, List.of(), List.of()));
                continue;
            }

            String aiRawResponse = openAiService.processLines(cleanedLines);
            String jsonText = extractTextContentFromOpenAiResponse(aiRawResponse);
            List<ClassifiedLine> classifiedLines = parseClassifiedLines(jsonText);
            Set<String> categorySet = new LinkedHashSet<>();
            for (ClassifiedLine classifiedLine : classifiedLines) {
                categorySet.add(classifiedLine.getCategory());
            }

            List<String> discoveredCategories = new ArrayList<>(categorySet);

            processedFiles.add(new ProcessedFileResult(fileName, discoveredCategories, classifiedLines));
            totalLines += cleanedLines.size();
        }

        List<CombinedCategoryGroup> combinedCategories = buildCombinedCategories(processedFiles);

        return new ProcessResponse(processedFiles, combinedCategories, processedFiles.size(), totalLines);
    }

    private List<String> extractCleanLines(String content) {
        String[] rawLines = content.split("\\r?\\n");
        List<String> cleanedLines = new ArrayList<>();

        for (String line : rawLines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                cleanedLines.add(trimmed);
            }
        }

        return cleanedLines;
    }

    private List<CombinedCategoryGroup> buildCombinedCategories(List<ProcessedFileResult> processedFiles) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (ProcessedFileResult fileResult : processedFiles) {
            for (ClassifiedLine item : fileResult.getItems()) {
                grouped.computeIfAbsent(item.getCategory(), k -> new ArrayList<>())
                        .add(item.getValue());
            }
        }

        List<CombinedCategoryGroup> combined = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            combined.add(new CombinedCategoryGroup(entry.getKey(), entry.getValue()));
        }

        return combined;
    }

    private String extractTextContentFromOpenAiResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode output = root.path("output");

        if (!output.isArray() || output.isEmpty()) {
            throw new OpenAiIntegrationException(HttpStatus.BAD_GATEWAY, "OpenAI response did not contain any output.");
        }

        for (JsonNode outputItem : output) {
            JsonNode content = outputItem.path("content");
            if (!content.isArray()) {
                continue;
            }

            for (JsonNode contentItem : content) {
                String text = contentItem.path("text").asText("").trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }

        throw new OpenAiIntegrationException(HttpStatus.BAD_GATEWAY, "OpenAI response did not contain any text output.");
    }

    private List<ClassifiedLine> parseClassifiedLines(String jsonText) throws IOException {
        List<Map<String, String>> parsedItems;
        try {
            parsedItems = objectMapper.readValue(jsonText, new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonMappingException e) {
            throw new OpenAiIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenAI returned text, but it was not valid classification JSON.",
                    e
            );
        } catch (IOException e) {
            throw new OpenAiIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenAI returned text, but it was not valid classification JSON.",
                    e
            );
        }

        List<ClassifiedLine> classifiedLines = new ArrayList<>();
        for (Map<String, String> item : parsedItems) {
            String value = item.getOrDefault("value", "").trim();
            if (value.isEmpty()) {
                continue;
            }

            String rawCategory = item.getOrDefault("category", "").trim();
            String category = rawCategory.isEmpty() ? "Uncategorized" : rawCategory;
            classifiedLines.add(new ClassifiedLine(value, category, value, 1.0));
        }

        if (classifiedLines.isEmpty()) {
            throw new OpenAiIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenAI did not return any usable classified lines."
            );
        }

        return classifiedLines;
    }
}
