package com.bobridze5.TeleMed_backend.api.dto.params.weight;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WeightRequest(
        @NotNull(message = "Значение веса обязательно")
        @Positive(message = "Значение должно быть больше нуля")
        Double value
) {
}
