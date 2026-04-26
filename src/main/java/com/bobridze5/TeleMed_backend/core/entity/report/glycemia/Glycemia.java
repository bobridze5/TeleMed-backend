package com.bobridze5.TeleMed_backend.core.entity.report.glycemia;

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
@Table(name = "glycemia")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Glycemia {
    @Id
    @Column(name = "glycemia_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "glycemia_level", nullable = false)
    private Double level;

    @Column(name = "glycemia_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GlycemiaType type;

    @Column(name = "glycemia_updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "glycemia_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
