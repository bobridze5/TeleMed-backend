package com.bobridze5.TeleMed_backend.core.service.appointment;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentCreationRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;

public interface AppointmentCreationStrategy {
    boolean supports(User initiator);

    Appointment create(User initiator, User target, AppointmentCreationRequest request);
}
