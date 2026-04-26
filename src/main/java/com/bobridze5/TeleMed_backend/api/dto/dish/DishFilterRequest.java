package com.bobridze5.TeleMed_backend.api.dto.dish;

import jakarta.validation.constraints.PositiveOrZero;

public record DishFilterRequest(
        String name,

        @PositiveOrZero
        Double minCal,
        @PositiveOrZero
        Double maxCal,

        @PositiveOrZero
        Double minProt,
        @PositiveOrZero
        Double maxProt,

        @PositiveOrZero
        Double minFat,
        @PositiveOrZero
        Double maxFat,

        @PositiveOrZero
        Double minCarb,
        @PositiveOrZero
        Double maxCarb
) {
}
