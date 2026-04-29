package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        String patientName,
        String doctorName,
        LocalDateTime appointmentDateTime,
        ConsultationType consultationType,
        AppointmentStatus status,
        String meetingLink,
        String meetingPhone,
        String meetingNotes,
        String reason,
        String confirmedBy,
        Boolean confirmedByPatient,
        Boolean confirmedByDoctor,
        LocalDateTime timestamp
) {
}
