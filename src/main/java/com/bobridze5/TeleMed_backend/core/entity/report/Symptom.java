package com.bobridze5.TeleMed_backend.core.entity.report;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "symptoms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Symptom {
    @Id
    @Column(name = "symptom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    private Patient patient;

    @Column(name = "symptom_severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private SymptomSeverity severity;

    @Column(name = "symptom_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "symptom_updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "symptom_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
