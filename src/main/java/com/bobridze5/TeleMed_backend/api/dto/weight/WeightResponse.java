package com.bobridze5.TeleMed_backend.api.dto.weight;

import java.time.LocalDateTime;

public record WeightResponse(
        Long id,
        Double value,
        LocalDateTime createAt
) {
}
