package com.bobridze5.TeleMed_backend.api.dto.dish;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateDishRequest(
        @Size(min = 1, message = "Имя не может быть пустым")
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
