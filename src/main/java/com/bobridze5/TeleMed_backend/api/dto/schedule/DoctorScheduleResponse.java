package com.bobridze5.TeleMed_backend.api.dto.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorScheduleResponse(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int slotDurationMinutes
) {
}
