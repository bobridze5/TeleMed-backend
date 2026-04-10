package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.BloodPressure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloodPressureRepository extends JpaRepository<BloodPressure, Long>, JpaSpecificationExecutor<BloodPressure> {
    List<BloodPressure> findByPatientIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long patientId, LocalDateTime start, LocalDateTime end
    );
}
