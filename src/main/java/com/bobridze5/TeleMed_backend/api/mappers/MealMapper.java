package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.MealItem;

public interface MealMapper {
    MealResponse toResponse(Meal meal);
    MealItemResponse toItemResponse(MealItem item);
}
