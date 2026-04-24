package com.bobridze5.TeleMed_backend.api.dto.medcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AllergyRequest(
        @NotBlank(message = "Аллерген обязателен")
        @Size(max = 255)
        String allergen,

        @Size(max = 500)
        String reaction
) {
}
