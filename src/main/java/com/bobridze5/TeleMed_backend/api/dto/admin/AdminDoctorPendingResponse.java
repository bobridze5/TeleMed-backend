package com.bobridze5.TeleMed_backend.api.dto.admin;

import java.time.LocalDateTime;

public record AdminDoctorPendingResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String middleName,
        String specialization,
        String organization,
        Integer experience,
        String qualification,
        LocalDateTime createdAt
) {
}
