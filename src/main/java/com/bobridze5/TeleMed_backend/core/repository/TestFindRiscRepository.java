package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.TestFindRisc;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TestFindRiscRepository extends JpaRepository<TestFindRisc, Long> {
    @Query("SELECT t FROM TestFindRisc t " +
            "WHERE t.id = :testId AND t.patient.id = :userId")
    Optional<TestFindRisc> findByIdAndUserId(
            @Param("testId") Long testId,
            @Param("userId") Long userId
    );

    @Query("SELECT t FROM TestFindRisc t " +
            "WHERE t.patient.id = :userId " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
            "ORDER BY t.createdAt DESC")
    Page<TestFindRisc> findAllByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
