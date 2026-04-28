package com.bobridze5.TeleMed_backend.api.dto.params.insulin;

import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record InsulinDoseRequest(
        @NotNull(message = "Количество единиц обязательно")
        @Positive(message = "Количество единиц должно быть больше нуля")
        Double units,

        Long mealId,

        @NotNull(message = "Тип инсулина обязателен")
        InsulinType insulinType,

        @NotNull(message = "Время инъекции обязательно")
        LocalDateTime takenAt,

        @Size(max = 500, message = "Заметка не может превышать 500 символов")
        String note
) {
}
