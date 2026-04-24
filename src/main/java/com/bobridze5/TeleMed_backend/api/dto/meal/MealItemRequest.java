package com.bobridze5.TeleMed_backend.api.dto.meal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MealItemRequest(
        @NotNull(message = "ID блюда обязательно")
        Long dishId,

        @NotNull
        @PositiveOrZero(message = "Порция не может быть отрицательной")
        Double portionGrams,

        String notes
) {
}
