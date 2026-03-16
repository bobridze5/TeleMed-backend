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
@Table(name = "weight")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weight implements Ownable {
    @Id
    @Column(name = "weight_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "weight_value", nullable = false)
    private Double value;

    @Column(name = "weight_updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "weight_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
