package com.example.apiecommerce.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<ApiError> handleException(Exception exc) {
        logger.error("Internal server error: {}", exc.getMessage(), exc);
        ApiError apiError = new ApiError("An unexpected error occurred: " + exc.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ApiError> handleNotFound(EntityNotFoundException exc) {
        logger.error("Entity not found: {}", exc.getMessage());
        ApiError apiError = new ApiError(exc.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage());
        }

        ApiError apiError = new ApiError("Invalid input", errors, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exc) {
        logger.error("Bad request: {}", exc.getMessage());
        ApiError apiError = new ApiError(exc.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleValidationException(HandlerMethodValidationException exc){
        logger.error("Validation failure: {}", exc.getMessage());

        ApiError apiError = new ApiError("Validation failure");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException exc){
        logger.error("Bad request: {}", exc.getMessage());
        ApiError apiError = new ApiError(exc.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }
}
