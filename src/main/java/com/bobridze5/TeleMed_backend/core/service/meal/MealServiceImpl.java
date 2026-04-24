package com.bobridze5.TeleMed_backend.core.service.meal;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.UpdateMealRequest;
import com.bobridze5.TeleMed_backend.api.mappers.MealMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import com.bobridze5.TeleMed_backend.core.entity.report.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.MealItem;
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
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final DishRepository dishRepository;
    private final MealMapper mealMapper;

    @Override
    @Transactional
    public MealResponse createMeal(Patient patient, MealRequest request) {
        Meal meal = Meal.builder()
                .patient(patient)
                .mealDatetime(request.mealDatetime())
                .mealType(request.mealType())
                .build();
        return mealMapper.toResponse(mealRepository.save(meal));
    }

    @Override
    @Transactional(readOnly = true)
    public MealResponse getMealById(Patient patient, Long mealId) {
        return mealMapper.toResponse(findMealForPatient(patient, mealId));
    }

    @Override
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

    @Override
    @Transactional
    public MealResponse updateMeal(Patient patient, Long mealId, UpdateMealRequest request) {
        Meal meal = findMealForPatient(patient, mealId);
        if (request.mealDatetime() != null) meal.setMealDatetime(request.mealDatetime());
        if (request.mealType() != null) meal.setMealType(request.mealType());
        return mealMapper.toResponse(mealRepository.save(meal));
    }

    @Override
    @Transactional
    public void deleteMeal(Patient patient, Long mealId) {
        mealRepository.delete(findMealForPatient(patient, mealId));
    }

    @Override
    @Transactional
    public MealItemResponse addItem(Patient patient, Long mealId, MealItemRequest request) {
        Meal meal = findMealForPatient(patient, mealId);
        Dish dish = dishRepository.findById(request.dishId())
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        MealItem item = MealItem.builder()
                .meal(meal)
                .dish(dish)
                .portionGrams(request.portionGrams())
                .notes(request.notes())
                .build();

        return mealMapper.toItemResponse(mealItemRepository.save(item));
    }

    @Override
    @Transactional
    public void removeItem(Patient patient, Long mealId, Long itemId) {
        findMealForPatient(patient, mealId);
        MealItem item = mealItemRepository.findByIdAndMealId(itemId, mealId)
                .orElseThrow(() -> new EntityNotFoundException("Позиция не найдена"));
        mealItemRepository.delete(item);
    }

    private Meal findMealForPatient(Patient patient, Long mealId) {
        return mealRepository.findByIdAndPatientId(mealId, patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Приём пищи не найден"));
    }
}
