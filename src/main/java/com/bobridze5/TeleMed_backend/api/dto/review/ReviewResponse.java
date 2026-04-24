package com.bobridze5.TeleMed_backend.api.dto.review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
