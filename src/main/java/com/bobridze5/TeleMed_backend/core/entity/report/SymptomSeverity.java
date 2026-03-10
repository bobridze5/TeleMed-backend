package com.bobridze5.TeleMed_backend.core.entity.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SymptomSeverity {
    NONE("Отсутствует"),
    MILD("Лёгкая"),
    MODERATE("Умеренная"),
    SEVERE("Тяжёлая"),
    CRITICAL("Критическая");

    private final String label;

    public String getName() {
        return name();
    }
}
