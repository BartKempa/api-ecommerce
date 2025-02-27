package com.example.apiecommerce.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ApiError {
    private String message;
    private LocalDateTime timestamp;
    private Map<String, List<String>> errors;

    public ApiError(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String message, Map<String, List<String>> errors, LocalDateTime timestamp) {
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
