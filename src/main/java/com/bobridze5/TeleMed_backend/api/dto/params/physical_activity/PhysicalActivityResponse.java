package com.bobridze5.TeleMed_backend.api.dto.params.physical_activity;

import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityType;

import java.time.LocalDateTime;

public record PhysicalActivityResponse(
        Long id,
        PhysicalActivityType activityType,
        PhysicalActivityIntensityType intensityType,
        Integer duration,
        LocalDateTime timestamp
) {
}
