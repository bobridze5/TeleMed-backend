package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.ConsultationType;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentDateTime,
        ConsultationType consultationType,
        AppointmentStatus status,
        String meetingLink,
        String reason,
        LocalDateTime timestamp
) {
}
