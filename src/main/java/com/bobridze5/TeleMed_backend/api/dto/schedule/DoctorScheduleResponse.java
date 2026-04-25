package com.bobridze5.TeleMed_backend.api.dto.schedule;

import java.time.DayOfWeek;
import java.util.List;

public record DoctorScheduleResponse(
        Long id,
        DayOfWeek dayOfWeek,
        List<SlotResponse> customSlots
) {
}
