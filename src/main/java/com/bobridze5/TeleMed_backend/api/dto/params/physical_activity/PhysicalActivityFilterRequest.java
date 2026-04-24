package com.bobridze5.TeleMed_backend.api.dto.params.physical_activity;

import com.bobridze5.TeleMed_backend.core.entity.report.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.PhysicalActivityType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PhysicalActivityFilterRequest(
        Integer page,
        Integer size,

        PhysicalActivityType activityType,
        PhysicalActivityIntensityType intensityType,

        Integer duration,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {
}
