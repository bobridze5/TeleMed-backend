package com.bobridze5.TeleMed_backend.api.dto.schedule;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalTime;

public record SlotResponse(
    Long id,
    LocalTime startTime,
    LocalTime endTime,
    ConsultationType consultationType
) {}
