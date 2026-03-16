package com.bobridze5.TeleMed_backend.core.entity.report;

import com.bobridze5.TeleMed_backend.core.entity.Ownable;
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
@Table(name = "blood_pressures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodPressure implements Ownable {
    @Id
    @Column(name = "blood_pressure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    private Patient patient;

    @Column(name = "blood_pressure_systolic", nullable = false)
    private Integer systolic;

    @Column(name = "blood_pressure_diastolic", nullable = false)
    private Integer diastolic;

    @Column(name = "blood_pressure_updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "blood_pressure_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
