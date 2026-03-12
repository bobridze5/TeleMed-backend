package com.bobridze5.TeleMed_backend.api.dto.params;

import java.time.LocalDateTime;

public interface DateFilter {
    LocalDateTime startDate();

    LocalDateTime endDate();

    default boolean isBetween() {
        return startDate() != null && endDate() != null;
    }
}
