package com.bobridze5.TeleMed_backend.api.dto.params.physical_activity;

import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityType;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record PhysicalActivityUpdateRequest(
        PhysicalActivityType activityType,
        PhysicalActivityIntensityType intensityType,
        @Positive(message = "Длительность в минутах должна быть больше нуля")
        Integer duration,
        LocalDateTime timestamp
) {
}
