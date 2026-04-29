package com.bobridze5.TeleMed_backend.api.dto.notification;

import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalTime;

public record ReminderRequest(
        @NotNull(message = "Тип напоминания обязателен")
        ReminderKind kind,

        @NotBlank(message = "Заголовок обязателен")
        @Size(max = 200, message = "Заголовок не может превышать 200 символов")
        String title,

        @Size(max = 1000, message = "Сообщение не может превышать 1000 символов")
        String message,

        Instant scheduledAt,

        LocalTime recurrenceTime,

        String recurrenceDays,

        Boolean enabled
) {
}
