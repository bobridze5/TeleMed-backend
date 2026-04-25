package com.bobridze5.TeleMed_backend.api.dto.params.physical_activity;

import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PhysicalActivityRequest(
        @NotNull(message = "Активность должна быть указана")
        PhysicalActivityType activityType,

        @NotNull(message = "Интенсивность должна быть указана")
        PhysicalActivityIntensityType intensityType,

        @NotNull(message = "Длительность должна быть указана")
        @Positive(message = "Длительность в минутах должна быть больше нуля")
        Integer duration

) {
}
