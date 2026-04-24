package com.bobridze5.TeleMed_backend.api.dto.meal;

import com.bobridze5.TeleMed_backend.core.entity.report.MealType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MealFilterRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        MealType mealType
) {
}
