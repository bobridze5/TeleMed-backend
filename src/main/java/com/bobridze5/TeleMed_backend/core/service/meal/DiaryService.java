package com.bobridze5.TeleMed_backend.core.service.meal;

import com.bobridze5.TeleMed_backend.api.dto.diary.NutritionDayResponse;
import com.bobridze5.TeleMed_backend.api.dto.diary.NutritionDayUpdateRequest;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.api.mappers.MealMapper;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.MealItem;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.NutritionDay;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.NutritionDayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final NutritionDayRepository nutritionDayRepository;
    private final MealMapper mealMapper;

    @Transactional
    public NutritionDayResponse getDay(Patient patient, LocalDate date) {
        return toResponse(getOrCreateDay(patient, date));
    }

    @Transactional(readOnly = true)
    public List<NutritionDayResponse> getRange(Patient patient, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Параметры from и to обязательны");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from не может быть позже to");
        }

        return nutritionDayRepository
                .findByPatientIdAndDiaryDateBetweenOrderByDiaryDateAsc(patient.getId(), from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NutritionDayResponse updateDay(Patient patient, LocalDate date, NutritionDayUpdateRequest request) {
        NutritionDay day = nutritionDayRepository
                .findByPatientIdAndDiaryDate(patient.getId(), date)
                .orElseThrow(() -> new EntityNotFoundException("Запись дневника за " + date + " не найдена"));

        if (request.completed() != null) day.setCompleted(request.completed());
        if (request.notes() != null) day.setNotes(request.notes());

        return toResponse(nutritionDayRepository.save(day));
    }

    @Transactional
    public NutritionDay getOrCreateDay(Patient patient, LocalDate date) {
        return nutritionDayRepository
                .findByPatientIdAndDiaryDate(patient.getId(), date)
                .orElseGet(() -> nutritionDayRepository.save(NutritionDay.builder()
                        .patient(patient)
                        .diaryDate(date)
                        .completed(false)
                        .totalCalories(0.0)
                        .totalCarbs(0.0)
                        .totalProtein(0.0)
                        .totalFats(0.0)
                        .build()));
    }

    @Transactional
    public void recomputeTotals(NutritionDay day) {
        double calories = 0, carbs = 0, protein = 0, fats = 0;
        for (Meal meal : day.getMeals()) {
            for (MealItem item : meal.getItems()) {
                double factor = (item.getPortionGrams() != null ? item.getPortionGrams() : 0) / 100.0;
                int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                calories += nz(item.getCalories()) * factor * qty;
                carbs += nz(item.getCarbs()) * factor * qty;
                protein += nz(item.getProtein()) * factor * qty;
                fats += nz(item.getFats()) * factor * qty;
            }
        }
        day.setTotalCalories(round(calories));
        day.setTotalCarbs(round(carbs));
        day.setTotalProtein(round(protein));
        day.setTotalFats(round(fats));
        nutritionDayRepository.save(day);
    }

    private NutritionDayResponse toResponse(NutritionDay day) {
        List<MealResponse> meals = day.getMeals() == null ? List.of()
                : day.getMeals().stream()
                  .sorted(Comparator.comparing(Meal::getMealDatetime, Comparator.nullsLast(Comparator.naturalOrder())))
                  .map(mealMapper::toResponse)
                  .toList();

        return new NutritionDayResponse(
                day.getId(),
                day.getDiaryDate(),
                day.getTotalCalories(),
                day.getTotalCarbs(),
                day.getTotalProtein(),
                day.getTotalFats(),
                day.getCompleted(),
                day.getNotes(),
                meals
        );
    }

    private double nz(Double v) {
        return v != null ? v : 0.0;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
