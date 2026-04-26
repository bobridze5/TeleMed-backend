package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.id = :appointmentId " +
            "AND (a.patient.id = :userId OR a.doctor.id = :userId)")
    Optional<Appointment> findByIdAndUserId(
            @Param("appointmentId") Long appointmentId,
            @Param("userId") Long userId
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.dateTime BETWEEN :startTime AND :endTime " +
            "AND a.status IN :statuses")
    List<Appointment> findAppointmentsInTimeRangeForNotifications(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    boolean existsByDoctorIdAndDateTimeAndStatusNot(Long doctorId, LocalDateTime dateTime, AppointmentStatus status);
}
