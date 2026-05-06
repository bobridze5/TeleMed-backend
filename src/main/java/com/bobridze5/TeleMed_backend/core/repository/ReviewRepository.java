package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByAppointmentId(Long appointmentId);
    Page<Review> findByDoctorIdOrderByCreatedAtDesc(Long doctorId, Pageable pageable);

    java.util.Optional<Review> findByAppointmentId(Long appointmentId);

    /**
     * Bulk-выборка id-шников отзывов по списку appointment_id — нужна, чтобы
     * за одну SQL-итерацию заполнить поле reviewId в AppointmentResponse для
     * целой страницы записей (без N+1).
     *
     * Возвращает строки [appointmentId, reviewId].
     */
    @Query("select r.appointment.id, r.id from Review r where r.appointment.id in :appointmentIds")
    List<Object[]> findReviewIdsByAppointmentIds(@Param("appointmentIds") java.util.Collection<Long> appointmentIds);

    /**
     * Агрегированная статистика отзывов врача: средняя оценка + количество.
     * Возвращает строки [avgRating (Double), count (Long)] — ровно одну,
     * т.к. без GROUP BY агрегат всегда возвращает единственный кортеж
     * (даже если отзывов нет: AVG=0, COUNT=0).
     *
     * Сигнатура — List, чтобы избежать поведения Hibernate 6.6, при
     * котором возврат скалярного multi-column запроса в виде Object[]
     * заворачивается в дополнительный массив (Object[][]).
     */
    @Query("select coalesce(avg(r.rating), 0), count(r) from Review r where r.doctor.id = :doctorId")
    List<Object[]> findRatingStatsByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Bulk-статистика для списка врачей — одной выборкой, чтобы избежать N+1
     * при отдаче списка врачей пациенту.
     *
     * Возвращает строки вида [doctorId (Long), avgRating (Double), count (Long)].
     */
    @Query("select r.doctor.id, avg(r.rating), count(r) from Review r " +
            "where r.doctor.id in :doctorIds group by r.doctor.id")
    List<Object[]> findRatingStatsByDoctorIds(@Param("doctorIds") Collection<Long> doctorIds);
}
