package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.Weight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {
    Page<Weight> findByPatientIdAndCreatedAtBetween(
            Long patientId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Weight> findByPatientId(Long patientId, Pageable pageable);
}
