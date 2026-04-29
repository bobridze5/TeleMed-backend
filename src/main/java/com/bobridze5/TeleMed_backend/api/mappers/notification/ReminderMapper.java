package com.bobridze5.TeleMed_backend.api.mappers.notification;

import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderRequest;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderResponse;
import com.bobridze5.TeleMed_backend.api.dto.notification.ReminderUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;

public final class ReminderMapper {

    private ReminderMapper() {}

    public static Reminder mapToEntity(ReminderRequest request, Patient patient) {
        return Reminder.builder()
                .id(null)
                .patient(patient)
                .kind(request.kind())
                .title(request.title())
                .message(request.message())
                .scheduledAt(request.scheduledAt())
                .recurrenceTime(request.recurrenceTime())
                .recurrenceDays(request.recurrenceDays())
                .enabled(request.enabled() != null ? request.enabled() : Boolean.TRUE)
                .build();
    }

    public static void updateEntity(ReminderUpdateRequest request, Reminder reminder) {
        if (request.title() != null) reminder.setTitle(request.title());
        if (request.message() != null) reminder.setMessage(request.message());
        if (request.scheduledAt() != null) reminder.setScheduledAt(request.scheduledAt());
        if (request.recurrenceTime() != null) reminder.setRecurrenceTime(request.recurrenceTime());
        if (request.recurrenceDays() != null) reminder.setRecurrenceDays(request.recurrenceDays());
        if (request.enabled() != null) reminder.setEnabled(request.enabled());
    }

    public static ReminderResponse mapToResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getKind(),
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getScheduledAt(),
                reminder.getRecurrenceTime(),
                reminder.getRecurrenceDays(),
                reminder.getEnabled(),
                reminder.getLastFiredAt(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
