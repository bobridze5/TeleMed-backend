package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.insulin.InsulinDose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsulinRepository extends JpaRepository<InsulinDose, Long>, JpaSpecificationExecutor<InsulinDose> {

    Page<InsulinDose> findByPatientId(Long patientId, Pageable pageable);

    Page<InsulinDose> findByPatientIdAndTakenAtBetween(
            Long patientId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Optional<InsulinDose> findByIdAndPatientId(Long id, Long patientId);

    List<InsulinDose> findByPatientIdAndTakenAtBetweenOrderByTakenAtAsc(
            Long patientId, LocalDateTime start, LocalDateTime end
    );
}
