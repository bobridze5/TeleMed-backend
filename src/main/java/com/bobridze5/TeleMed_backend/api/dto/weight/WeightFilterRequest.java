package com.bobridze5.TeleMed_backend.api.dto.weight;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record WeightFilterRequest(
        int page,
        int size,

        // TODO: Поправить формат. 2026-03-11T09:27:05.615148
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {

    public boolean isBetween() {
        return startDate != null && endDate != null;
    }
}
