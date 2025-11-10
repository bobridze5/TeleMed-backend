package com.devops_labs.userService.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException e){
        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", e.getStatus().value(),
                "error", e.getStatus().getReasonPhrase(),
                "message", e.getMessage()
        );

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .orElse("Invalid Request");

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", e.getCause().getMessage(),
                "message", message
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception e){
        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "message", e.getMessage()
        );

        return ResponseEntity.status(500).body(response);
    }
}
