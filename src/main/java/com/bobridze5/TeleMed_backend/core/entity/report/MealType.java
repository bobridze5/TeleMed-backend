package com.bobridze5.TeleMed_backend.core.entity.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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
