package com.bobridze5.TeleMed_backend.api.dto.params.glycemia;

import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;

import java.time.LocalDateTime;

public record GlycemiaResponse(
        Long id,
        Double level,
        GlycemiaType type,
        LocalDateTime timestamp
) {
}
