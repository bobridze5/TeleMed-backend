package com.bobridze5.TeleMed_backend.api.dto.weight;

import java.time.LocalDateTime;

public record WeightFilterRequest(
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
