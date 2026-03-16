package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.PatientDoctorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientDoctorAssignmentRepository extends JpaRepository<PatientDoctorAssignment, Long> {
    @Query("SELECT COUNT(p) > 0 FROM PatientDoctorAssignment p WHERE p.doctor.id = :doctorId AND p.patient.id = :patientId")
    boolean existsAssignment(@Param("doctor_id") Long doctorId, @Param("patient_id") Long patientId);
}
