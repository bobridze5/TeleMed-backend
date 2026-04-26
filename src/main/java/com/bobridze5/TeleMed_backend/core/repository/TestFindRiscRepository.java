package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.TestFindRisc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestFindRiscRepository extends JpaRepository<TestFindRisc, Long>, JpaSpecificationExecutor<TestFindRisc> {
    @Query("SELECT t FROM TestFindRisc t " +
            "WHERE t.id = :testId AND t.patient.id = :userId")
    Optional<TestFindRisc> findByIdAndUserId(
            @Param("testId") Long testId,
            @Param("userId") Long userId
    );
}
