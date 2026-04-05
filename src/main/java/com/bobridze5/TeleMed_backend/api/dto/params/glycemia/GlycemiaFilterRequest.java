package com.bobridze5.TeleMed_backend.api.dto.params.glycemia;

import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record GlycemiaFilterRequest(
        int page,
        int size,

        GlycemiaType type,

        Double level,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime start,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime end
) {
}
