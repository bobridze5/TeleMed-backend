package com.bobridze5.TeleMed_backend.core.entity.report;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "glycemia_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlycemiaRecord {
    @Id
    @Column(name = "glycemia_report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "glycemia_report_level", nullable = false)
    private Double level;

    @Column(name = "glycemia_report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GlycemiaReportType type;

    @Column(name = "glycemia_report_created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
