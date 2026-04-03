package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.ConsultationType;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record AppointmentUpdateRequest(
        @Future(message = "Запись возможна только на будущее время")
        LocalDateTime dateTime,
        ConsultationType consultationType,
        String meetingLink,
        String reason
) {
}

/*
@Id
    @Column(name = "appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_consultation_type", nullable = false)
    private ConsultationType consultationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false)
    private AppointmentStatus status;

    @Column(name = "appointment_link")
    private String meetingLink;

    @Column(name = "appointment_reason", columnDefinition = "TEXT")
    private String reason;

    @UpdateTimestamp
    @Column(name = "appointment_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "appointment_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 */