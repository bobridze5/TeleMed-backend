package com.bobridze5.TeleMed_backend.api.controllers.patient.eat;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.UpdateMealRequest;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.meal.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Дневник питания", description = "Управление приёмами пищи и их составом")
public class MealController {

    private final MealService mealService;

    @PostMapping(API.PATIENT_ME_MEALS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить приём пищи")
    public MealResponse createMeal(
            @CurrentPatient Patient patient,
            @RequestBody @Valid MealRequest request
    ) {
        return mealService.createMeal(patient, request);
    }

    @GetMapping(API.PATIENT_ME_MEALS)
    @Operation(summary = "Список приёмов пищи")
    public Page<MealResponse> getMeals(
            @CurrentPatient Patient patient,
            @ParameterObject @ModelAttribute MealFilterRequest filter,
            Pageable pageable
    ) {
        return mealService.getMeals(patient, filter, pageable);
    }

    @GetMapping(API.PATIENT_ME_MEAL_ID)
    @Operation(summary = "Приём пищи по ID")
    public MealResponse getMeal(
            @CurrentPatient Patient patient,
            @PathVariable Long mealId
    ) {
        return mealService.getMealById(patient, mealId);
    }

    @PutMapping(API.PATIENT_ME_MEAL_ID)
    @Operation(summary = "Обновить приём пищи")
    public MealResponse updateMeal(
            @CurrentPatient Patient patient,
            @PathVariable Long mealId,
            @RequestBody @Valid UpdateMealRequest request
    ) {
        return mealService.updateMeal(patient, mealId, request);
    }

    @DeleteMapping(API.PATIENT_ME_MEAL_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить приём пищи")
    public void deleteMeal(
            @CurrentPatient Patient patient,
            @PathVariable Long mealId
    ) {
        mealService.deleteMeal(patient, mealId);
    }

    @PostMapping(API.PATIENT_ME_MEAL_ITEMS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить блюдо в приём пищи")
    public MealItemResponse addItem(
            @CurrentPatient Patient patient,
            @PathVariable Long mealId,
            @RequestBody @Valid MealItemRequest request
    ) {
        return mealService.addItem(patient, mealId, request);
    }

    @DeleteMapping(API.PATIENT_ME_MEAL_ITEM_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить блюдо из приёма пищи")
    public void removeItem(
            @CurrentPatient Patient patient,
            @PathVariable Long mealId,
            @PathVariable Long itemId
    ) {
        mealService.removeItem(patient, mealId, itemId);
    }
}
