package com.bobridze5.TeleMed_backend.api.dto.admin;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;

import java.time.LocalDateTime;

public record AdminDoctorResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String middleName,
        String specialization,
        String organization,
        Integer experience,
        String qualification,
        UserStatus status,
        LocalDateTime createdAt
) {
}
