package com.ibeanny.aisorter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeanny.aisorter.model.ClassifiedLine;
import com.ibeanny.aisorter.model.CombinedCategoryGroup;
import com.ibeanny.aisorter.model.ProcessResponse;
import com.ibeanny.aisorter.model.ProcessedFileResult;
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

            String aiRawResponse = openAiService.processLines(cleanedLines);

            String jsonText = extractTextContentFromOpenAiResponse(aiRawResponse);

            List<Map<String, String>> parsedItems = objectMapper.readValue(
                    jsonText,
                    new TypeReference<List<Map<String, String>>>() {}
            );

            List<ClassifiedLine> classifiedLines = new ArrayList<>();
            Set<String> categorySet = new LinkedHashSet<>();

            for (Map<String, String> item : parsedItems) {
                String category = item.getOrDefault("category", "Uncategorized");
                String value = item.getOrDefault("value", "");

                categorySet.add(category);
                classifiedLines.add(new ClassifiedLine(value, category, value, 1.0));
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
            throw new RuntimeException("OpenAI response missing output array.");
        }

        JsonNode firstMessage = output.get(0);
        JsonNode content = firstMessage.path("content");

        if (!content.isArray() || content.isEmpty()) {
            throw new RuntimeException("OpenAI response missing content array.");
        }

        JsonNode firstContent = content.get(0);
        JsonNode textNode = firstContent.path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new RuntimeException("OpenAI response missing text field.");
        }

        return textNode.asText();
    }
}