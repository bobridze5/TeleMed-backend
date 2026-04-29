package com.bobridze5.TeleMed_backend.api.dto.params.glycemia;

import com.bobridze5.TeleMed_backend.core.entity.report.glycemia.GlycemiaType;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record GlycemiaUpdateRequest(
        @Positive(message = "Уровень гликемии не может быть отрицательным")
        Double level,

        GlycemiaType type,

        LocalDateTime timestamp
) {
}
