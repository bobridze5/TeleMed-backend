package com.bobridze5.TeleMed_backend.api.dto.params.symptom;

import com.bobridze5.TeleMed_backend.core.entity.report.SymptomSeverity;
import jakarta.validation.constraints.NotNull;

public record SymptomRequest(
    @NotNull(message = "Степень серьёзности должна быть указана")
    SymptomSeverity severity,

    String description
) {

}
