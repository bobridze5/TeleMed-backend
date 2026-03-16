package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_doctor_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"patient_id", "doctor_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDoctorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @CreationTimestamp
    private LocalDateTime assignedAt;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

}

