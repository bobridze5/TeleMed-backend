package com.bobridze5.TeleMed_backend.api.dto.meal;

import com.bobridze5.TeleMed_backend.core.entity.report.food.MealType;

import java.time.LocalDateTime;

public record UpdateMealRequest(
        LocalDateTime mealDatetime,
        MealType mealType
) {
}
