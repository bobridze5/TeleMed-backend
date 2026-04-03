package com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure;

import java.time.LocalDateTime;

public record BloodPressureResponse(
        Long id,
        Integer systolic,
        Integer diastolic,
        LocalDateTime createdAt
) {
}
