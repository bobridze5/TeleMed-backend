package com.bobridze5.TeleMed_backend.api.dto.meal;

import com.bobridze5.TeleMed_backend.api.dto.dish.DishResponse;

public record MealItemResponse(
        Long id,
        DishResponse dish,
        Double portionGrams,
        String notes,
        Double caloriesTotal,
        Double carbsTotal,
        Double proteinTotal,
        Double fatsTotal
) {
}
