package com.bobridze5.TeleMed_backend.core.service.meal;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.UpdateMealRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MealService {
    MealResponse createMeal(Patient patient, MealRequest request);
    MealResponse getMealById(Patient patient, Long mealId);
    Page<MealResponse> getMeals(Patient patient, MealFilterRequest filter, Pageable pageable);
    MealResponse updateMeal(Patient patient, Long mealId, UpdateMealRequest request);
    void deleteMeal(Patient patient, Long mealId);

    MealItemResponse addItem(Patient patient, Long mealId, MealItemRequest request);
    void removeItem(Patient patient, Long mealId, Long itemId);
}
