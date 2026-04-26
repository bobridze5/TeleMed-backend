package com.bobridze5.TeleMed_backend.api.dto.schedule;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ScheduleSlotDto(
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull ConsultationType consultationType
) {
}
