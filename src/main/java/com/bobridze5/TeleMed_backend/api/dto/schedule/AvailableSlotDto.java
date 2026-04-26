package com.bobridze5.TeleMed_backend.api.dto.schedule;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalDateTime;

public record AvailableSlotDto(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        ConsultationType consultationType
) {
}
