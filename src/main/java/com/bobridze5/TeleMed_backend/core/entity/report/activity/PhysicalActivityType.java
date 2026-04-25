package com.bobridze5.TeleMed_backend.core.entity.report.activity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhysicalActivityType {
    // Кардио (Аэробные)
    WALKING("Ходьба"),
    RUNNING("Бег"),
    CYCLING("Велосипед"),
    SWIMMING("Плавание"),
    CARDIO_TRAINING("Кардио-тренировка"),

    // Силовые (Анаэробные)
    STRENGTH_TRAINING("Силовая тренировка"),
    // TODO: собственный вес?
    BODYWEIGHT_EXERCISES("Собственный вес"),
    CROSSFIT("Кроссфит"),

    // Гибкость и баланс
    YOGA("Йога"),
    STRETCHING("Растяжка"),
    PILATES("Пилатес"),

    // Спорт и игры
    TEAM_SPORTS("Командные игры"),
    MARTIAL_ARTS("Единоборства"),
    DANCING("Танцы"),

    // Прочее
    OTHER("Другое");

    @JsonValue
    private final String label;

    public String getName() {
        return name();
    }
}
