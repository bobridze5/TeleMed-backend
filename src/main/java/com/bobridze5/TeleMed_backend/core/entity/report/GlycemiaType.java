package com.bobridze5.TeleMed_backend.core.entity.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlycemiaType {
    FASTING("Натощак"),
    BEFORE_BREAKFAST("Перед завтраком"),
    AFTER_BREAKFAST("Через 2 часа после завтрака"),

    BEFORE_LUNCH("Перед обедом"),
    AFTER_LUNCH("Через 2 часа после обеда"),

    BEFORE_DINNER("Перед ужином"),
    AFTER_DINNER("Через 2 часа после ужина"),

    BEFORE_BEDTIME("Перед сном"),
    NIGHT("Ночью"),

    BEFORE_ACTIVITY("Перед нагрузкой"),
    AFTER_ACTIVITY("После нагрузки"),
    FEELING_UNWELL("При плохом самочувствии"),
    OTHER("Другое");

    private final String label;

    public String getName() {
        return name();
    }
}
