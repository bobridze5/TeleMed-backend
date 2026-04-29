package com.bobridze5.TeleMed_backend.api.dto.params.insulin;

import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record InsulinDoseUpdateRequest(
        @Positive(message = "Количество единиц должно быть больше нуля")
        Double units,

        Long mealId,
        InsulinType insulinType,
        Instant takenAt,

        @Size(max = 500, message = "Заметка не может превышать 500 символов")
        String note
) {
}
