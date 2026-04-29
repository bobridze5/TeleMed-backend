package com.bobridze5.TeleMed_backend.api.dto.notification;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long reminderId,
        String title,
        String message,
        String type,
        Boolean isRead,
        Instant createdAt
) {
}
