package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivity;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityIntensityType;
import com.bobridze5.TeleMed_backend.core.entity.report.activity.PhysicalActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PhysicalActivityRepository extends JpaRepository<PhysicalActivity, Long> {
    Optional<PhysicalActivity> findByIdAndPatientId(Long id, Long patientId);

    @Query("SELECT p FROM PhysicalActivity p WHERE p.patient.id = :patientId " +
            "AND (:activity IS NULL OR p.activityType = :activity) " +
            "AND (:intensity IS NULL OR p.intensityType = :intensity) " +
            "AND (:duration IS NULL OR p.duration >= :duration) " +
            "AND (CAST(:start AS timestamp) IS NULL OR p.createdAt >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR p.createdAt <= :end)")
    Page<PhysicalActivity> findWithFilter(
            @Param("patientId") Long patientId,
            @Param("activity") PhysicalActivityType activity,
            @Param("intensity") PhysicalActivityIntensityType intensity,
            @Param("duration") Integer duration,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );


}
