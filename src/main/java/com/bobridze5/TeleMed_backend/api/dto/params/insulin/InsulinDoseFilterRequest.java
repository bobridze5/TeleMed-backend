package com.bobridze5.TeleMed_backend.api.dto.params.insulin;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinType;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record InsulinDoseFilterRequest(
        Integer page,
        Integer size,

        InsulinType insulinType,

        @PositiveOrZero
        Double minUnits,

        @PositiveOrZero
        Double maxUnits,

        Long mealId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) implements DateFilter {
}
