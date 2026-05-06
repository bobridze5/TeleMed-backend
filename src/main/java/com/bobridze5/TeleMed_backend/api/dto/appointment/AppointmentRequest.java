package com.bobridze5.TeleMed_backend.api.dto.appointment;

import com.bobridze5.TeleMed_backend.core.entity.medical.ConsultationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long targetId,

        @NotNull(message = "Дата и время обязательны")
        @Future(message = "Запись возможна только на будущее время")
        LocalDateTime dateTime,

        @NotNull(message = "Выберите ти консультации")
        ConsultationType consultationType,

        // Опциональная длительность слота в минутах. Имеет смысл только когда
        // запись создаёт сам врач и слота ещё нет в расписании — в этом случае
        // бэкенд создаёт слот с указанной длительностью. Если не передано —
        // используется значение по умолчанию (30 минут). Для записи со стороны
        // пациента поле игнорируется: пациент выбирает из готовых слотов.
        Integer slotDurationMinutes
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