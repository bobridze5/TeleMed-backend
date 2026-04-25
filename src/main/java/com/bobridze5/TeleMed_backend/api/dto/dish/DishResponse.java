package com.bobridze5.TeleMed_backend.api.dto.dish;

public record DishResponse(
        Long id,
        String name,
        Double calories,
        Double carbs,
        Double protein,
        Double fats,
        String photoURL,
        String description
) {
}
