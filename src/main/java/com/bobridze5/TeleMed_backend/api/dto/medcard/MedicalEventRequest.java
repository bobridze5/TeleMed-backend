package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MedicalEventRequest(
        @NotBlank(message = "Описание события обязательно")
        String description,

        @NotNull(message = "Дата события обязательна")
        LocalDate eventDate,

        @Size(max = 100)
        String eventType,

        LocalDate endDate
) {
}
