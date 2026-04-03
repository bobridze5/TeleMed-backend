package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.Glycemia;
import com.bobridze5.TeleMed_backend.core.entity.report.GlycemiaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GlycemiaRepository extends JpaRepository<Glycemia, Long> {
    Optional<Glycemia> findByIdAndPatientId(Long id, Long patientId);

    @Query("SELECT g FROM Glycemia g WHERE g.patient.id = :patientId " +
            "AND (:type IS NULL OR g.type = :type) " +
            "AND (:level IS NULL OR g.level >= :level) " +
            "AND (CAST(:start AS timestamp) IS NULL OR g.updatedAt >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR g.updatedAt <= :end)")
    Page<Glycemia> findWithFilter(
            @Param("patientId") Long patientId,
            @Param("type") GlycemiaType type,
            @Param("level") Double level,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

}
