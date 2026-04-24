package com.bobridze5.TeleMed_backend.api.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull(message = "Оценка обязательна")
        @Min(value = 1, message = "Минимальная оценка — 1")
        @Max(value = 5, message = "Максимальная оценка — 5")
        Integer rating,

        String comment
) {
}
