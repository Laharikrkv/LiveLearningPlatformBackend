package com.example.live_learning.common.exceptions;


import lombok.Getter;
import java.time.Instant;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> validationErrors;

    
    public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

   
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }
}