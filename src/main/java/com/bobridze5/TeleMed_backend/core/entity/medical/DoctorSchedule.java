package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("startTime ASC")
    @Builder.Default
    private List<ScheduleSlot> customSlots = new ArrayList<>();
}
