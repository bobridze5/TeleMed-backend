package com.bobridze5.TeleMed_backend.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Стандартный ответ с описанием ошибки")
public record ApiErrorResponse(
        @Schema(description = "Временная метка ошибки") Instant timestamp,
        @Schema(description = "HTTP-код статуса", example = "404") int status,
        @Schema(description = "Краткое описание статуса", example = "Not Found") String error,
        @Schema(description = "Детальное сообщение об ошибке") String message
) {
}
