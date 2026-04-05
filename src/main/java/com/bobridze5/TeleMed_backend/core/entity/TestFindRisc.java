package com.bobridze5.TeleMed_backend.core.entity;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestFindRisc {
    @Id
    @Column(name = "test_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Patient patient;

    @Column(name = "test_points_age", nullable = false)
    private Integer agePoints;

    @Column(name = "test_points_bmi", nullable = false)
    private Integer bodyMassIndexPoints;

    @Column(name = "test_points_waist", nullable = false)
    private Integer waistCircumferencePoints;

    @Column(name = "test_points_vegetable", nullable = false)
    private Integer vegetablePoints;

    @Column(name = "test_points_activity", nullable = false)
    private Integer activityPoints;

    @Column(name = "test_points_hypertension", nullable = false)
    private Integer hypertensionPoints;

    @Column(name = "test_points_glucose", nullable = false)
    private Integer glucosePoints;

    @Column(name = "test_points_history", nullable = false)
    private Integer familyHistoryPoints;

    @Column(name = "test_points_total", nullable = false)
    private Integer totalPoints;

    @Column(name = "test_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
