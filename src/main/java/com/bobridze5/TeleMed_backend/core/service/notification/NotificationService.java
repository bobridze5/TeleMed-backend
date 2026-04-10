package com.bobridze5.TeleMed_backend.core.service.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;

public interface NotificationService {
    void sendConfirmationRequest(Appointment appointment);
}
