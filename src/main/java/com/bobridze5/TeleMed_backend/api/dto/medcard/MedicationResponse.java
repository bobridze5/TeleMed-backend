package com.bobridze5.TeleMed_backend.api.dto.medcard;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicationResponse(
        Long id,
        String name,
        String dosage,
        String frequency,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active,
        LocalDateTime createdAt
) {
}
