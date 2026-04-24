package com.bobridze5.TeleMed_backend.api.dto.meal;

import com.bobridze5.TeleMed_backend.core.entity.report.MealType;

import java.time.LocalDateTime;
import java.util.List;

public record MealResponse(
        Long id,
        LocalDateTime mealDatetime,
        MealType mealType,
        List<MealItemResponse> items,
        Double totalCalories,
        Double totalCarbs,
        Double totalProtein,
        Double totalFats
) {
}
