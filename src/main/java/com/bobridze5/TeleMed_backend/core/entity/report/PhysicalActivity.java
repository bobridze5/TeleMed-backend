package com.bobridze5.TeleMed_backend.core.entity.report;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "physical_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalActivity {
    @Id
    @Column(name = "physical_activity_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "physical_activity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PhysicalActivityType type;

    @Column(name = "physical_activity_duration", nullable = false)
    private Integer duration;

    @Column(name = "physical_activity_intensity", nullable = false)
    @Enumerated(EnumType.STRING)
    private PhysicalActivityIntensityType intensityType;

    @Column(name = "physical_activity_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
