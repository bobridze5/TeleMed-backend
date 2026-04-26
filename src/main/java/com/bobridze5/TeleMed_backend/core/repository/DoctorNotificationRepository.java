package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.DoctorNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorNotificationRepository extends JpaRepository<DoctorNotification, Long> {
    Page<DoctorNotification> findByDoctorIdOrderByCreatedAtDesc(Long doctorId, Pageable pageable);
    long countByDoctorIdAndReadFalse(Long doctorId);
}
