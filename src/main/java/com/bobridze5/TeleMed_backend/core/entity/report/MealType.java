package com.bobridze5.TeleMed_backend.core.entity.report;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MealType {
    BREAKFAST("Завтрак"),
    BRUNCH("Поздний завтрак"),
    LUNCH("Обед"),
    DINNER("Ужин"),
    SUPPER("Поздний ужин"),
    SNACK("Перекус");

    @JsonValue
    private final String label;

    public String getName() {
        return name();
    }

}
