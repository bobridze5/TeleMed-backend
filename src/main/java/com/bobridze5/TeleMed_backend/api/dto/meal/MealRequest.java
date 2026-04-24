package com.bobridze5.TeleMed_backend.api.dto.meal;

import com.bobridze5.TeleMed_backend.core.entity.report.MealType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MealRequest(
        @NotNull(message = "Дата и время обязательны")
        LocalDateTime mealDatetime,

        @NotNull(message = "Тип приёма пищи обязателен")
        MealType mealType
) {
}
