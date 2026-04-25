package com.bobridze5.TeleMed_backend.api.dto.diary;

import jakarta.validation.constraints.Size;

public record NutritionDayUpdateRequest(
        Boolean completed,

        @Size(max = 5000)
        String notes
) {
}
