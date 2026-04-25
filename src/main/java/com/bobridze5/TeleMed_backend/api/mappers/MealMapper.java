package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.meal.MealItemResponse;
import com.bobridze5.TeleMed_backend.api.dto.meal.MealResponse;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.Dish;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.MealItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

@Component
@RequiredArgsConstructor
public class MealMapper {

    private final DishMapper dishMapper;

    public MealResponse toResponse(Meal meal) {
        List<MealItem> items = meal.getItems() != null ? meal.getItems() : Collections.emptyList();
        List<MealItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        double totalCalories = sum(itemResponses, MealItemResponse::caloriesTotal);
        double totalCarbs    = sum(itemResponses, MealItemResponse::carbsTotal);
        double totalProtein  = sum(itemResponses, MealItemResponse::proteinTotal);
        double totalFats     = sum(itemResponses, MealItemResponse::fatsTotal);

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

    public MealItemResponse toItemResponse(MealItem item) {
        Dish dish = item.getDish();
        double portion = nz(item.getPortionGrams());
        int qty = item.getQuantity() != null ? item.getQuantity() : 1;
        double factor = portion / 100.0 * qty;

        return new MealItemResponse(
                item.getId(),
                dish != null ? dishMapper.mapToResponse(dish) : null,
                item.getPortionGrams(),
                item.getQuantity(),
                round(nz(item.getCalories()) * factor),
                round(nz(item.getCarbs())    * factor),
                round(nz(item.getProtein())  * factor),
                round(nz(item.getFats())     * factor),
                item.getBreadUnits()
        );
    }

    private double sum(List<MealItemResponse> items, ToDoubleFunction<MealItemResponse> getter) {
        return items.stream()
                .filter(r -> r != null)
                .mapToDouble(r -> {
                    double v = getter.applyAsDouble(r);
                    return Double.isNaN(v) ? 0 : v;
                })
                .sum();
    }

    private double nz(Double v) {
        return v != null ? v : 0.0;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
