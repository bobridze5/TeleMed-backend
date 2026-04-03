package com.bobridze5.TeleMed_backend.api.dto.params.symptom;

import com.bobridze5.TeleMed_backend.core.entity.report.SymptomSeverity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record SymptomFilterRequest(
        int page,
        int size,

        SymptomSeverity severity,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {
}
