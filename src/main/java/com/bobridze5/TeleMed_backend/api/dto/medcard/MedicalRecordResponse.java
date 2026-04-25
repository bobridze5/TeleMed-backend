package com.bobridze5.TeleMed_backend.api.dto.medcard;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
        Long id,
        Long patientId,
        Long doctorId,
        String doctorName,
        Long appointmentId,
        String title,
        String complaints,
        String diagnosis,
        String recommendations,
        String prescriptions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
