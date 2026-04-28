package com.bobridze5.TeleMed_backend.core.service.meal;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.UpdateMealRequest;
import com.bobridze5.TeleMed_backend.api.mappers.MealMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Dish;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.food.MealItem;
import com.bobridze5.TeleMed_backend.core.entity.report.food.NutritionDay;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.DishRepository;
import com.bobridze5.TeleMed_backend.core.repository.MealItemRepository;
import com.bobridze5.TeleMed_backend.core.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final DishRepository dishRepository;
    private final MealMapper mealMapper;
    private final DiaryService diaryService;

    @Transactional(readOnly = true)
    public MealResponse getMealById(Patient patient, Long mealId) {
        return mealMapper.toResponse(findMealForPatient(patient, mealId));
    }

    @Transactional(readOnly = true)
    public Page<MealResponse> getMeals(Patient patient, MealFilterRequest filter, Pageable pageable) {
        Long patientId = patient.getId();

        if (filter.date() != null && filter.mealType() != null) {
            LocalDateTime from = filter.date().atStartOfDay();
            LocalDateTime to = filter.date().plusDays(1).atStartOfDay();
            return mealRepository
                    .findByPatientIdAndMealDatetimeBetweenAndMealType(patientId, from, to, filter.mealType(), pageable)
                    .map(mealMapper::toResponse);
        }
        if (filter.date() != null) {
            LocalDateTime from = filter.date().atStartOfDay();
            LocalDateTime to = filter.date().plusDays(1).atStartOfDay();
            return mealRepository
                    .findByPatientIdAndMealDatetimeBetween(patientId, from, to, pageable)
                    .map(mealMapper::toResponse);
        }
        if (filter.mealType() != null) {
            return mealRepository
                    .findByPatientIdAndMealType(patientId, filter.mealType(), pageable)
                    .map(mealMapper::toResponse);
        }
        return mealRepository.findByPatientId(patientId, pageable).map(mealMapper::toResponse);
    }

    @Transactional
    public MealResponse createMeal(Patient patient, MealRequest request) {
        if (request.mealDatetime() == null) {
            throw new IllegalArgumentException("Дата и время приёма пищи обязательны");
        }

        NutritionDay day = diaryService.getOrCreateDay(patient, request.mealDatetime().toLocalDate());

        Meal meal = Meal.builder()
                .patient(patient)
                .nutritionDay(day)
                .mealDatetime(request.mealDatetime())
                .mealType(request.mealType())
                .build();

        Meal saved = mealRepository.save(meal);
        diaryService.recomputeTotals(day);
        return mealMapper.toResponse(saved);
    }

    @Transactional
    public MealResponse updateMeal(Patient patient, Long mealId, UpdateMealRequest request) {
        Meal meal = findMealForPatient(patient, mealId);

        if (request.mealType() != null) {
            meal.setMealType(request.mealType());
        }

        boolean dateChanged = false;
        if (request.mealDatetime() != null) {
            dateChanged = meal.getMealDatetime() == null
                    || !meal.getMealDatetime().toLocalDate().equals(request.mealDatetime().toLocalDate());
            meal.setMealDatetime(request.mealDatetime());
        }

        NutritionDay previousDay = meal.getNutritionDay();
        if (dateChanged) {
            NutritionDay newDay = diaryService.getOrCreateDay(patient, request.mealDatetime().toLocalDate());
            meal.setNutritionDay(newDay);
        }

        Meal saved = mealRepository.save(meal);

        if (dateChanged && previousDay != null) {
            diaryService.recomputeTotals(previousDay);
        }
        if (saved.getNutritionDay() != null) {
            diaryService.recomputeTotals(saved.getNutritionDay());
        }

        return mealMapper.toResponse(saved);
    }

    @Transactional
    public void deleteMeal(Patient patient, Long mealId) {
        Meal meal = findMealForPatient(patient, mealId);
        NutritionDay day = meal.getNutritionDay();
        mealRepository.delete(meal);
        if (day != null) {
            diaryService.recomputeTotals(day);
        }
    }

    @Transactional
    public MealItemResponse addItem(Patient patient, Long mealId, MealItemRequest request) {
        Meal meal = findMealForPatient(patient, mealId);
        Dish dish = dishRepository.findById(request.dishId())
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        MealItem item = MealItem.builder()
                .meal(meal)
                .dish(dish)
                .portionGrams(request.portionGrams())
                .quantity(request.quantity() != null ? request.quantity() : 1)
                .calories(nz(dish.getCalories()))
                .carbs(nz(dish.getCarbs()))
                .protein(nz(dish.getProtein()))
                .fats(nz(dish.getFats()))
                .breadUnits(estimateBreadUnits(dish, request.portionGrams()))
                .build();

        MealItem saved = mealItemRepository.save(item);

        if (meal.getNutritionDay() != null) {
            // Перечитать через JPA, чтобы агрегат увидел новый item
            meal.getItems().add(saved);
            diaryService.recomputeTotals(meal.getNutritionDay());
        }

        return mealMapper.toItemResponse(saved);
    }

    @Transactional
    public void removeItem(Patient patient, Long mealId, Long itemId) {
        Meal meal = findMealForPatient(patient, mealId);
        MealItem item = mealItemRepository.findByIdAndMealId(itemId, mealId)
                .orElseThrow(() -> new EntityNotFoundException("Позиция не найдена"));

        mealItemRepository.delete(item);
        meal.getItems().removeIf(i -> i.getId().equals(itemId));

        if (meal.getNutritionDay() != null) {
            diaryService.recomputeTotals(meal.getNutritionDay());
        }
    }

    private Meal findMealForPatient(Patient patient, Long mealId) {
        return mealRepository.findByIdAndPatientId(mealId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Приём пищи не найден"));
    }

    private double nz(Double v) {
        return v != null ? v : 0.0;
    }

    // 1 ХЕ ≈ 12 г углеводов. Считаем для портии (граммах) на базе углеводов на 100 г.
    private double estimateBreadUnits(Dish dish, Double portionGrams) {
        if (dish == null || dish.getCarbs() == null || portionGrams == null) return 0.0;
        return Math.round(dish.getCarbs() * portionGrams / 100.0 / 12.0 * 10.0) / 10.0;
    }
}
