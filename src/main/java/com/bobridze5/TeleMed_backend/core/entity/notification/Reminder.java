package com.bobridze5.TeleMed_backend.core.entity.notification;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "reminder")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @Id
    @Column(name = "reminder_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_kind", nullable = false, length = 20)
    private ReminderKind kind;

    @Column(name = "reminder_title", nullable = false, length = 200)
    private String title;

    @Column(name = "reminder_message", length = 1000)
    private String message;

    @Column(name = "reminder_scheduled_at")
    private Instant scheduledAt;

    @Column(name = "reminder_recurrence_time")
    private LocalTime recurrenceTime;

    @Column(name = "reminder_recurrence_days", length = 120)
    private String recurrenceDays;

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "reminder_last_fired_at")
    private Instant lastFiredAt;

    @CreationTimestamp
    @Column(name = "reminder_created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "reminder_updated_at")
    private Instant updatedAt;
}
