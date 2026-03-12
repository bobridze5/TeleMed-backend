package com.bobridze5.TeleMed_backend.api.dto.params.weight;

import com.bobridze5.TeleMed_backend.api.dto.params.DateFilter;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record WeightFilterRequest(
        int page,
        int size,

        @PositiveOrZero
        Double minWeight,

        @PositiveOrZero
        Double maxWeight,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) implements DateFilter {
}
