package com.bobridze5.TeleMed_backend.api.dto.params.physical_activity;

import com.bobridze5.TeleMed_backend.core.entity.report.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.PhysicalActivityType;
import jakarta.validation.constraints.Positive;

public record PhysicalActivityUpdateRequest(
        PhysicalActivityType activityType,
        PhysicalActivityIntensityType intensityType,
        @Positive(message = "Длительность в минутах должна быть больше нуля")
        Integer duration
) {
}
