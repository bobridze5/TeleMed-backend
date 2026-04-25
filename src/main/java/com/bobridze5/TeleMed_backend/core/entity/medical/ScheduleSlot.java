package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_schedule_slot_time",
                columnNames = {"schedule_id", "start_time", "end_time", "consultation_type"}
        ),
        indexes = {
                @Index(name = "idx_schedule_slots_schedule_id", columnList = "schedule_id"),
                @Index(name = "idx_schedule_slots_start_time", columnList = "start_time")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private DoctorSchedule schedule;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type", nullable = false, length = 16)
    private ConsultationType consultationType;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
