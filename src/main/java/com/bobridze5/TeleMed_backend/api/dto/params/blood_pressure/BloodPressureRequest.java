package com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record BloodPressureRequest(
        // TODO: Значения валидации
        @Max(300)
        @NotNull(message = "Систолическое давление обязательно")
        @Positive(message = "Не может быть меньше нуля")
        Integer systolic,

        @Max(200)
        @NotNull(message = "Диастолическое давление обязательно")
        @Positive(message = "Не может быть меньше нуля")
        Integer diastolic,

        LocalDateTime timestamp
) {
}
