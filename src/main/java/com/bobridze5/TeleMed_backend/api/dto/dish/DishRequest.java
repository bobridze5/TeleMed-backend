package com.bobridze5.TeleMed_backend.api.dto.dish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.URL;

public record DishRequest(
        @NotBlank(message = "Название обязательно")
        String name,

        @PositiveOrZero
        Double calories,

        @PositiveOrZero
        Double carbs,

        @PositiveOrZero
        Double protein,

        @PositiveOrZero
        Double fats,

        @URL(message = "Неправильный URL формат")
        String photoURL,
        String description
) {
}
