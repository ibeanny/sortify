package com.ibeanny.aisorter.exception;

import org.springframework.http.HttpStatus;

public class OpenAiIntegrationException extends RuntimeException {
    private final HttpStatus status;

    public OpenAiIntegrationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public OpenAiIntegrationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
