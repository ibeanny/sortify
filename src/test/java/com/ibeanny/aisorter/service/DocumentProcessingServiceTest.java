package com.ibeanny.aisorter.service;

import com.ibeanny.aisorter.exception.OpenAiIntegrationException;
import com.ibeanny.aisorter.model.ProcessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentProcessingServiceTest {

    @Test
    void processFilesBuildsCombinedCategoriesFromAiOutput() throws IOException, InterruptedException {
        OpenAiService openAiService = mock(OpenAiService.class);
        DocumentProcessingService service = new DocumentProcessingService(openAiService);
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "notes.txt",
                "text/plain",
                "Alice Smith\n555-1234\n".getBytes()
        );

        when(openAiService.processLines(java.util.List.of("Alice Smith", "555-1234"))).thenReturn("""
                {
                  "output": [
                    {
                      "content": [
                        {
                          "text": "[{\\"category\\":\\"Person\\",\\"value\\":\\"Alice Smith\\"},{\\"category\\":\\"Contact\\",\\"value\\":\\"555-1234\\"}]"
                        }
                      ]
                    }
                  ]
                }
                """);

        ProcessResponse response = service.processFiles(new MockMultipartFile[]{file});

        assertEquals(1, response.getTotalFiles());
        assertEquals(2, response.getTotalLines());
        assertEquals(2, response.getCombinedCategories().size());
        assertEquals("Person", response.getCombinedCategories().get(0).getCategory());
        assertEquals("Alice Smith", response.getCombinedCategories().get(0).getValues().get(0));
    }

    @Test
    void processFilesRejectsMissingTextInAiResponse() throws IOException, InterruptedException {
        OpenAiService openAiService = mock(OpenAiService.class);
        DocumentProcessingService service = new DocumentProcessingService(openAiService);
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "notes.txt",
                "text/plain",
                "Alice Smith\n".getBytes()
        );

        when(openAiService.processLines(java.util.List.of("Alice Smith"))).thenReturn("""
                {
                  "output": [
                    {
                      "content": []
                    }
                  ]
                }
                """);

        OpenAiIntegrationException exception = assertThrows(
                OpenAiIntegrationException.class,
                () -> service.processFiles(new MockMultipartFile[]{file})
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
    }
}
