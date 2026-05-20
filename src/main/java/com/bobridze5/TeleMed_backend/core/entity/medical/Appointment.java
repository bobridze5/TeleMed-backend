package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
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

    /**
     * TODO: добавить {@code endDateTime} или {@code durationMinutes},
     * иначе невозможно точно проверить пересечение с другими приёмами
     * (см. DoctorScheduleService.validateSlotNotTaken).
     */
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime dateTime;

    /**
     * Тип консультации на момент бронирования (snapshot).
     * Если врач позже изменит/удалит слот в расписании, это поле
     * останется неизменным — так и задумано (исторические данные).
     * Связь с конкретным слотом пока не хранится (bookedSlotId).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_consultation_type", nullable = false)
    private ConsultationType consultationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false)
    private AppointmentStatus status;

    @Column(name = "appointment_link")
    private String meetingLink;

    @Column(name = "appointment_meeting_phone", length = 64)
    private String meetingPhone;

    @Column(name = "appointment_meeting_notes", columnDefinition = "TEXT")
    private String meetingNotes;

    @Column(name = "appointment_reason", columnDefinition = "TEXT")
    private String reason;

    @Builder.Default
    @Column(name = "appointment_confirmed_by_patient", nullable = false)
    private Boolean confirmedByPatient = false;

    @Builder.Default
    @Column(name = "appointment_confirmed_by_doctor", nullable = false)
    private Boolean confirmedByDoctor = false;

    @UpdateTimestamp
    @Column(name = "appointment_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "appointment_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
