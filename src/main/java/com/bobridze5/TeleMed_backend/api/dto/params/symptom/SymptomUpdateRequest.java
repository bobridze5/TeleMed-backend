package com.bobridze5.TeleMed_backend.api.dto.params.symptom;

import com.bobridze5.TeleMed_backend.core.entity.report.SymptomSeverity;

public record SymptomUpdateRequest(
        SymptomSeverity severity,
        String description
) {
}
