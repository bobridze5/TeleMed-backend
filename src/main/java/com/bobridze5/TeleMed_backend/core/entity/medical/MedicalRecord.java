package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointment_id")
    private Appointment appointment;

    @Column(name = "record_title", nullable = false, length = 255)
    private String title;

    @Column(name = "record_complaints", columnDefinition = "TEXT")
    private String complaints;

    @Column(name = "record_diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "record_recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "record_prescriptions", columnDefinition = "TEXT")
    private String prescriptions;

    @CreationTimestamp
    @Column(name = "record_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "record_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
