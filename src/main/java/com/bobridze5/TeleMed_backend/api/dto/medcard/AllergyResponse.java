package com.bobridze5.TeleMed_backend.api.dto.medcard;

import java.time.LocalDateTime;

public record AllergyResponse(
        Long id,
        String allergen,
        String reaction,
        LocalDateTime createdAt
) {
}
