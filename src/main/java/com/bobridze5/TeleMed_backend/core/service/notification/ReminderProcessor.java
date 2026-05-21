package com.bobridze5.TeleMed_backend.core.service.notification;

public interface ReminderProcessor {
    void processOneShots();
    void processRecurring();
}
