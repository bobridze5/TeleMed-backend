package com.bobridze5.TeleMed_backend.api.dto.params.glycemia;

import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;
import jakarta.validation.constraints.Positive;

public record GlycemiaUpdateRequest(
        @Positive(message = "Уровень гликемии не может быть отрицательным")
        Double level,

        GlycemiaType type
) {
}
