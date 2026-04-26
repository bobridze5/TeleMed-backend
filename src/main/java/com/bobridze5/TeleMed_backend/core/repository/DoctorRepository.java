package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d " +
            "LEFT JOIN FETCH d.specialization " +
            "LEFT JOIN FETCH d.organization o " +
            "LEFT JOIN FETCH o.city " +
            "WHERE (:specializationId IS NULL OR d.specialization.id = :specializationId) " +
            "AND (:cityId IS NULL OR o.city.id = :cityId)")
    Page<Doctor> findAllFiltered(
            @Param("specializationId") Long specializationId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );

    Page<Doctor> findByStatus(UserStatus status, Pageable pageable);

    long countByStatus(UserStatus status);
}
