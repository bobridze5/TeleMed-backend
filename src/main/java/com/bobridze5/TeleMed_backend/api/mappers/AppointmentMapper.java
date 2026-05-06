package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentRequest;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentResponse;
import com.bobridze5.TeleMed_backend.api.dto.appointment.AppointmentUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.repository.MedicalRecordRepository;
import com.bobridze5.TeleMed_backend.core.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public final class AppointmentMapper {

    private final MedicalRecordRepository medicalRecordRepository;
    private final ReviewRepository reviewRepository;

    private String buildName(User user) {
        if (user == null) return null;
        return Stream.of(user.getLastName(), user.getFirstName(), user.getMiddleName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    /**
     * Маппинг одного приёма — точечно подгружаем reviewId и medicalRecordId
     * (по одному запросу каждый). Для страниц используйте
     * {@link #mapToResponse(Appointment, Long, Long)} с заранее подготовленными
     * map'ами, чтобы избежать N+1.
     */
    public AppointmentResponse mapToResponse(Appointment appointment) {
        Long medicalRecordId = medicalRecordRepository
                .findRecordIdsByAppointmentIds(java.util.List.of(appointment.getId()))
                .stream().findFirst()
                .map(row -> ((Number) row[1]).longValue())
                .orElse(null);
        Long reviewId = reviewRepository
                .findByAppointmentId(appointment.getId())
                .map(r -> r.getId())
                .orElse(null);
        return mapToResponse(appointment, medicalRecordId, reviewId);
    }

    public AppointmentResponse mapToResponse(Appointment appointment, Long medicalRecordId, Long reviewId) {
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
                appointment.getMeetingPhone(),
                appointment.getMeetingNotes(),
                appointment.getReason(),
                appointment.getConfirmedBy(),
                Boolean.TRUE.equals(appointment.getConfirmedByPatient()),
                Boolean.TRUE.equals(appointment.getConfirmedByDoctor()),
                appointment.getUpdatedAt(),
                medicalRecordId,
                reviewId
        );
    }

    /**
     * Helper для маппинга страницы записей: за один запрос вытаскивает все
     * связанные medical records и reviews, далее быстро маппит каждый item.
     */
    public Map<Long, Long> loadMedicalRecordIds(java.util.Collection<Long> appointmentIds) {
        if (appointmentIds.isEmpty()) return Collections.emptyMap();
        return medicalRecordRepository.findRecordIdsByAppointmentIds(appointmentIds).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    public Map<Long, Long> loadReviewIds(java.util.Collection<Long> appointmentIds) {
        if (appointmentIds.isEmpty()) return Collections.emptyMap();
        return reviewRepository.findReviewIdsByAppointmentIds(appointmentIds).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    public Appointment mapToEntity(AppointmentRequest request, Patient patient, Doctor doctor) {
        return Appointment.builder()
                .id(null)
                .patient(patient)
                .doctor(doctor)
                .consultationType(request.consultationType())
                .status(AppointmentStatus.CREATED)
                .dateTime(request.dateTime())
                .confirmedByPatient(false)
                .confirmedByDoctor(false)
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
        if (request.meetingPhone() != null) {
            appointment.setMeetingPhone(request.meetingPhone());
        }
        if (request.meetingNotes() != null) {
            appointment.setMeetingNotes(request.meetingNotes());
        }
        if (request.reason() != null) {
            appointment.setReason(request.reason());
        }
    }
}
