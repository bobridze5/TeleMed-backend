package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Page<MedicalRecord> findByPatientId(Long patientId, Pageable pageable);

    List<MedicalRecord> findTop5ByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<MedicalRecord> findByIdAndPatientId(Long id, Long patientId);

    Optional<MedicalRecord> findByIdAndDoctorId(Long id, Long doctorId);

    /**
     * Используется для проверки «одна запись на приём» при создании новой
     * MedicalRecord — врач не должен иметь возможности добавить две записи
     * на один и тот же appointment.
     */
    boolean existsByAppointmentId(Long appointmentId);

    /**
     * Bulk-выборка id-шников записей консультаций по списку appointment_id.
     * Используется для заполнения поля medicalRecordId в AppointmentResponse
     * за одну SQL-итерацию.
     */
    @org.springframework.data.jpa.repository.Query(
            "select mr.appointment.id, mr.id from MedicalRecord mr " +
                    "where mr.appointment.id in :appointmentIds")
    List<Object[]> findRecordIdsByAppointmentIds(
            @org.springframework.data.repository.query.Param("appointmentIds") java.util.Collection<Long> appointmentIds);
}
