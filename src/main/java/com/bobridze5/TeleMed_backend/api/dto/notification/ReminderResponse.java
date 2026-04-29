package com.bobridze5.TeleMed_backend.api.dto.notification;

import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;

import java.time.Instant;
import java.time.LocalTime;

public record ReminderResponse(
        Long id,
        ReminderKind kind,
        String title,
        String message,
        Instant scheduledAt,
        LocalTime recurrenceTime,
        String recurrenceDays,
        Boolean enabled,
        Instant lastFiredAt,
        Instant createdAt,
        Instant updatedAt
) {
}
