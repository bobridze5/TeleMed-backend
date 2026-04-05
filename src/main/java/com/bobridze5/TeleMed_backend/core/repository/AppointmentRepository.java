package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.AppointmentStatus;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.id = :appointmentId " +
            "AND (a.patient.id = :userId OR a.doctor.id = :userId)")
    Optional<Appointment> findByIdAndUserId(
            @Param("appointmentId") Long appointmentId,
            @Param("userId") Long userId
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE (a.patient.id = :userId OR a.doctor.id = :userId)")
    Page<Appointment> findByPatientIdOrDoctorId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE (a.patient.id = :userId OR a.doctor.id = :userId) " +
            "AND a.dateTime BETWEEN :startTime AND :endTime")
    Page<Appointment> findByPatientIdOrDoctorIdAndDateTimeBetween(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.dateTime BETWEEN :startTime AND :endTime " +
            "AND a.status IN :statuses")
    List<Appointment> findAppointmentsInTimeRangeForNotifications(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );
}
