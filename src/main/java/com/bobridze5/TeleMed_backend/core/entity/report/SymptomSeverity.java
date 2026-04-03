package com.bobridze5.TeleMed_backend.core.entity.report;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SymptomSeverity {
    NONE("Отсутствует"),
    MILD("Лёгкая"),
    MODERATE("Умеренная"),
    SEVERE("Тяжёлая"),
    CRITICAL("Критическая");

    @JsonValue
    private final String label;

    public String getName() {
        return name();
    }
}
