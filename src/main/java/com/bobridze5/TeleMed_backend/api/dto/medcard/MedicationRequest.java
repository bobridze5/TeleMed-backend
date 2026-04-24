package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MedicationRequest(
        @NotBlank(message = "Название препарата обязательно")
        @Size(max = 255)
        String name,

        @Size(max = 100)
        String dosage,

        @Size(max = 100)
        String frequency,

        LocalDate startDate,

        LocalDate endDate,

        Boolean active
) {
}
