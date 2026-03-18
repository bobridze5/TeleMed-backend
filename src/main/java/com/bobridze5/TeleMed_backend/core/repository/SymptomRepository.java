package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.Symptom;
import com.bobridze5.TeleMed_backend.core.entity.report.SymptomSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long>, JpaSpecificationExecutor<Symptom> {
    Optional<Symptom> findByIdAndPatientId(Long id, Long patientId);


    @Query("SELECT s FROM Symptom s WHERE s.patient.id = :patientId " +
            "AND (:severity IS NULL OR s.severity = :severity) " +
            "AND (CAST(:start AS timestamp) IS NULL OR s.createdAt >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR s.createdAt <= :end)")
    Page<Symptom> findWithFilter(
            @Param("patientId") Long patientId,
            @Param("severity") SymptomSeverity severity,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
