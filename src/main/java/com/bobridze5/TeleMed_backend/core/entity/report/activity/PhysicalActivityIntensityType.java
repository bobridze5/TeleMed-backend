package com.bobridze5.TeleMed_backend.core.entity.report.activity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhysicalActivityIntensityType {
    LOW("Низкая"),
    MEDIUM("Умеренная"),
    HARD("Высокая");

    @JsonValue
    private final String label;

    public String getName() {
        return name();
    }
}
