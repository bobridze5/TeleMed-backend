package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {
    Page<MedicalEvent> findByPatientIdOrderByEventDateDesc(Long patientId, Pageable pageable);
    Optional<MedicalEvent> findByIdAndPatientId(Long id, Long patientId);
}
