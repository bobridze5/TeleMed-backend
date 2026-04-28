package com.bobridze5.TeleMed_backend.api.dto.diary;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;

import java.time.LocalDate;
import java.util.List;

public record NutritionDayResponse(
        Long id,
        LocalDate diaryDate,
        Double totalCalories,
        Double totalCarbs,
        Double totalProtein,
        Double totalFats,
        Boolean completed,
        String notes,
        List<MealResponse> meals,
        Double totalBreadUnits
) {
}
