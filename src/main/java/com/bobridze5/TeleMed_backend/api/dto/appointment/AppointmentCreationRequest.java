package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalDateTime;

public sealed interface AppointmentCreationRequest
        permits PatientAppointmentRequest, DoctorAppointmentRequest {

    Long targetId();

    LocalDateTime dateTime();

    ConsultationType consultationType();
}
