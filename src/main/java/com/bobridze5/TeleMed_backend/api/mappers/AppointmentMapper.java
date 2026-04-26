package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public final class AppointmentMapper {

    private String buildName(User user) {
        if (user == null) return null;
        return Stream.of(user.getLastName(), user.getFirstName(), user.getMiddleName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    public AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                buildName(appointment.getPatient()),
                buildName(appointment.getDoctor()),
                appointment.getDateTime(),
                appointment.getConsultationType(),
                appointment.getStatus(),
                appointment.getMeetingLink(),
                appointment.getReason(),
                appointment.getConfirmedBy(),
                appointment.getUpdatedAt()
        );
    }

    public Appointment mapToEntity(AppointmentRequest request, Patient patient, Doctor doctor) {
        return Appointment.builder()
                .id(null)
                .patient(patient)
                .doctor(doctor)
                .consultationType(request.consultationType())
                .status(AppointmentStatus.CREATED)
                .dateTime(request.dateTime())
                .build();
    }

    public void updateEntity(AppointmentUpdateRequest request, Appointment appointment) {
        if (request.dateTime() != null) {
            appointment.setDateTime(request.dateTime());
        }
        if (request.consultationType() != null) {
            appointment.setConsultationType(request.consultationType());
        }
        if (request.meetingLink() != null) {
            appointment.setMeetingLink(request.meetingLink());
        }
        if (request.reason() != null) {
            appointment.setReason(request.reason());
        }
    }
}

/*
    Long id,
    Long patientId,
    Long doctorId,
    LocalDateTime dateTime,
    ConsultationType consultationType,
    AppointmentStatus status,
    String meetingLink,
    String reason,
    LocalDateTime timestamp
 */