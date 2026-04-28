package com.bobridze5.TeleMed_backend.core.entity.report.insulin;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Meal;
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
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "insulin_dose")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsulinDose {
    @Id
    @Column(name = "insulin_dose_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", referencedColumnName = "meal_id")
    private Meal meal;

    @Positive
    @Column(name = "insulin_dose_units", nullable = false)
    private Double units;

    @Enumerated(EnumType.STRING)
    @Column(name = "insulin_dose_type", nullable = false)
    private InsulinType insulinType;

    @Column(name = "insulin_dose_note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "insulin_dose_taken_at", nullable = false)
    private LocalDateTime takenAt;

    @UpdateTimestamp
    @Column(name = "insulin_dose_updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "insulin_dose_created_at")
    private LocalDateTime createdAt;

}
