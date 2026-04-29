package com.bobridze5.TeleMed_backend.api.mappers.notification;

import com.bobridze5.TeleMed_backend.api.dto.notification.NotificationResponse;
import com.bobridze5.TeleMed_backend.core.entity.notification.Notification;

public final class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getReminder() != null ? notification.getReminder().getId() : null,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
