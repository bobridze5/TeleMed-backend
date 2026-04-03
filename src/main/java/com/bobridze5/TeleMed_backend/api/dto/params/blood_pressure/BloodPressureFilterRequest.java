package com.bobridze5.TeleMed_backend.api.dto.params.blood_pressure;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record BloodPressureFilterRequest(
        int page,
        int size,

        @PositiveOrZero
        Integer minSystolic,

        @PositiveOrZero
        Integer maxSystolic,

        @PositiveOrZero
        Integer minDiastolic,

        @PositiveOrZero
        Integer maxDiastolic,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) implements DateFilter {

}
