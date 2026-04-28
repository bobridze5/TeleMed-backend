package com.bobridze5.TeleMed_backend.core.entity.report.food;

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

    private final String label;

    public String getName() {
        return name();
    }

}
