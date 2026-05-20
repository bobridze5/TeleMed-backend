package com.bobridze5.TeleMed_backend.api.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException e){
        log.warn("[ApiException] status={} message={}", e.getStatus(), e.getMessage());
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
        // Собираем мапу «поле → первое сообщение об ошибке». Бин-валидация
        // может вернуть несколько ошибок на одном поле (например, @Positive +
        // @NotNull), но фронту достаточно показать одну строку под полем —
        // остальные не теряем смыслово, т.к. правки одного поля обычно
        // решают и связанные нарушения.
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Некорректное значение",
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        // Топ-уровневое message оставляем для совместимости с клиентами,
        // которые ещё не научились читать fieldErrors (а также для общих
        // тостов «что-то не так в форме»).
        String message = fieldErrors.entrySet().stream()
                .findFirst()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .orElse("Invalid Request");

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", message);
        response.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Неверный тип параметра '%s'. Ожидалось: %s",
                e.getName(),
                e.getRequiredType().getSimpleName());

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", message
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {

        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("Validation failed");

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", message
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadable] {}", e.getMessage());
        String message = "Неверный формат тела запроса";
        Throwable cause = e.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            message = "Неверный формат поля '" + ife.getPath().get(0).getFieldName() + "'";
        }
        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", message
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("[DataIntegrityViolation] {}", e.getMessage());
        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", HttpStatus.CONFLICT.getReasonPhrase(),
                "message", "Конфликт данных (возможно, запись уже существует)"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("[MaxUploadSizeExceeded] {}", e.getMessage());
        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "error", HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
                "message", "Файл слишком большой"
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}
