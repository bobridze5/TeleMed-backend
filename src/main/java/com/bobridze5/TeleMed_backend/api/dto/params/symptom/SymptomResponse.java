package com.bobridze5.TeleMed_backend.api.dto.params.symptom;

import com.bobridze5.TeleMed_backend.core.entity.report.symptom.SymptomSeverity;

import java.time.LocalDateTime;

public record SymptomResponse(
        Long id,
        SymptomSeverity severity,
        String description,
        LocalDateTime timestamp
) {
}
