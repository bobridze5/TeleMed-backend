package com.bobridze5.TeleMed_backend.api.dto.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record DoctorScheduleRequest(
        @NotNull
        DayOfWeek dayOfWeek,

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        @Min(5)
        @Max(240)
        Integer slotDurationMinutes,

        @Valid
        List<ScheduleSlotDto> customSlots
) {
}
