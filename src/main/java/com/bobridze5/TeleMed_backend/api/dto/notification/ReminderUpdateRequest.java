package com.bobridze5.TeleMed_backend.api.dto.notification;

import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalTime;

public record ReminderUpdateRequest(
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
