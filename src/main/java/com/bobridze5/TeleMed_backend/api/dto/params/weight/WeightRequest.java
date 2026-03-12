package com.bobridze5.TeleMed_backend.api.dto.params.weight;

import jakarta.validation.constraints.Positive;

public record WeightRequest(
        @Positive(message = "Значение должно быть больше нуля")
        Double value
) {
}
