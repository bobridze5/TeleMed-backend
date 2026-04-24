package com.bobridze5.TeleMed_backend.api.dto.medcard;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicalEventResponse(
        Long id,
        String description,
        LocalDate eventDate,
        String eventType,
        LocalDate endDate,
        LocalDateTime createdAt
) {
}
