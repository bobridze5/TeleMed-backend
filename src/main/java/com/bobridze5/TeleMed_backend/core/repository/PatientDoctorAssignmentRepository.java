package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientDoctorAssignmentRepository extends JpaRepository<PatientDoctorAssignment, Long> {

    @Query("SELECT COUNT(a) > 0 FROM PatientDoctorAssignment a " +
            "WHERE a.doctor.id = :doctorId AND a.patient.id = :patientId")
    boolean existsAssignment(@Param("doctorId") Long doctorId, @Param("patientId") Long patientId);

    @Query("SELECT a FROM PatientDoctorAssignment a " +
            "JOIN FETCH a.patient " +
            "WHERE a.doctor.id = :doctorId AND a.active = true")
    Page<PatientDoctorAssignment> findActiveByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
}
