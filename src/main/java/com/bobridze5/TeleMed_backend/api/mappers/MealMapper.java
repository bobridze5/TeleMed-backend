package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.food.MealItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.bobridze5.TeleMed_backend.core.service.utils.Constant.Nutrition;

@Component
@RequiredArgsConstructor
public class MealMapper {

    private final DishMapper dishMapper;

    public MealResponse toResponse(Meal meal) {
        List<MealItem> items = meal.getItems() != null ? meal.getItems() : Collections.emptyList();
        List<MealItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        return new MealResponse(
                meal.getId(),
                meal.getMealDatetime(),
                meal.getMealType(),
                itemResponses,
                round(meal.totalCalories()),
                round(meal.totalCarbs()),
                round(meal.totalProtein()),
                round(meal.totalFats()),
                Nutrition.toBreadUnits(meal.totalCarbs())
        );
    }

    public MealItemResponse toItemResponse(MealItem item) {
        return new MealItemResponse(
                item.getId(),
                item.getDish() != null ? dishMapper.mapToResponse(item.getDish()) : null,
                item.getPortionGrams(),
                item.getQuantity(),
                round(item.totalCalories()),
                round(item.totalCarbs()),
                round(item.totalProtein()),
                round(item.totalFats()),
                Nutrition.toBreadUnits(item.totalCarbs())
        );
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
