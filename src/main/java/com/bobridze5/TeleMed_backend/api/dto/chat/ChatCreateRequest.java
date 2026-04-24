package com.bobridze5.TeleMed_backend.api.dto.chat;

import jakarta.validation.constraints.NotNull;

public record ChatCreateRequest(
        @NotNull(message = "ID пользователя обязателен")
        Long userId
) {
}
