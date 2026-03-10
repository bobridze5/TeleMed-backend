package com.bobridze5.TeleMed_backend.core.entity.report;

public enum PhysicalActivityType {
    // Кардио (Аэробные)
    WALKING,            // Ходьба
    RUNNING,            // Бег
    CYCLING,            // Велосипед
    SWIMMING,           // Плавание
    CARDIO_TRAINING,    // Общая кардио-тренировка (эллипс, степпер)

    // Силовые (Анаэробные)
    STRENGTH_TRAINING,  // Силовая тренировка (зал, веса)
    BODYWEIGHT_EXERCISES, // Работа с собственным весом (турники, отжимания)
    CROSSFIT,           // Кроссфит

    // Гибкость и баланс
    YOGA,               // Йога
    STRETCHING,         // Растяжка
    PILATES,            // Пилатес

    // Спорт и игры
    TEAM_SPORTS,        // Командные игры (футбол, баскетбол)
    MARTIAL_ARTS,       // Единоборства
    DANCING,            // Танцы

    // Прочее
    OTHER               // Другое
}
