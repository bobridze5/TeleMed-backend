package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import com.bobridze5.TeleMed_backend.core.entity.report.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.MealItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MealMapperImpl implements MealMapper {

    private final DishMapper dishMapper;

    @Override
    public MealResponse toResponse(Meal meal) {
        List<MealItem> items = meal.getItems() != null ? meal.getItems() : Collections.emptyList();
        List<MealItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        double totalCalories = itemResponses.stream()
                .mapToDouble(r -> r.caloriesTotal() != null ? r.caloriesTotal() : 0)
                .sum();
        double totalCarbs = itemResponses.stream()
                .mapToDouble(r -> r.carbsTotal() != null ? r.carbsTotal() : 0)
                .sum();
        double totalProtein = itemResponses.stream()
                .mapToDouble(r -> r.proteinTotal() != null ? r.proteinTotal() : 0)
                .sum();
        double totalFats = itemResponses.stream()
                .mapToDouble(r -> r.fatsTotal() != null ? r.fatsTotal() : 0)
                .sum();

        return new MealResponse(
                meal.getId(),
                meal.getMealDatetime(),
                meal.getMealType(),
                itemResponses,
                round(totalCalories),
                round(totalCarbs),
                round(totalProtein),
                round(totalFats)
        );
    }

    @Override
    public MealItemResponse toItemResponse(MealItem item) {
        Dish dish = item.getDish();
        double portion = item.getPortionGrams() != null ? item.getPortionGrams() : 0;
        double factor = portion / 100.0;

        Double calories = dish.getCalories() != null ? round(dish.getCalories() * factor) : null;
        Double carbs = dish.getCarbs() != null ? round(dish.getCarbs() * factor) : null;
        Double protein = dish.getProtein() != null ? round(dish.getProtein() * factor) : null;
        Double fats = dish.getFats() != null ? round(dish.getFats() * factor) : null;

        return new MealItemResponse(
                item.getId(),
                dishMapper.mapToResponse(dish),
                portion,
                item.getNotes(),
                calories,
                carbs,
                protein,
                fats
        );
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
