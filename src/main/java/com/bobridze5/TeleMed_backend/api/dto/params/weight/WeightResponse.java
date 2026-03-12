package com.bobridze5.TeleMed_backend.api.dto.params.weight;

import java.time.LocalDateTime;

public record WeightResponse(
        Long id,
        Double value,
        LocalDateTime createAt
) {
}
