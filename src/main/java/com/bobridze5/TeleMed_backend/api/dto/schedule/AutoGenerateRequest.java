package com.bobridze5.TeleMed_backend.api.dto.schedule;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record AutoGenerateRequest(
        @NotNull
        LocalTime fromTime,

        @NotNull
        LocalTime toTime,

        @NotNull
        @Min(value = 5, message = "Минимальная длительность слота — 5 минут")
        @Max(value = 240, message = "Максимальная длительность слота — 240 минут")
        Integer slotMinutes,

        @NotNull
        ConsultationType consultationType
) {
}
