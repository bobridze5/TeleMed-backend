package com.bobridze5.TeleMed_backend.api.dto.params.symptom;

import com.bobridze5.TeleMed_backend.core.entity.report.symptom.SymptomSeverity;

import java.time.LocalDateTime;

public record SymptomUpdateRequest(
        SymptomSeverity severity,
        String description,
        LocalDateTime timestamp
) {
}
