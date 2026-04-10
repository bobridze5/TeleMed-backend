package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "schedule_day_of_week"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {

    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "schedule_start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "schedule_end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "schedule_slot_duration_minutes", nullable = false)
    private int slotDurationMinutes;
}
