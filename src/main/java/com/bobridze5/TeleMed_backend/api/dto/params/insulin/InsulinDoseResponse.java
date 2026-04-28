package com.bobridze5.TeleMed_backend.api.dto.params.insulin;

import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinType;

import java.time.LocalDateTime;

public record InsulinDoseResponse(
        Long id,
        Long mealId,
        Double units,
        InsulinType insulinType,
        String note,
        LocalDateTime takenAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
