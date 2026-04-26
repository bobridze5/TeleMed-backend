package com.bobridze5.TeleMed_backend.api.dto.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.List;

public record DoctorScheduleRequest(
        @NotNull
        DayOfWeek dayOfWeek,

        @Valid
        List<ScheduleSlotDto> customSlots
) {
}
