package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.eat.Meal;
import com.bobridze5.TeleMed_backend.core.entity.report.eat.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    Page<Meal> findByPatientId(Long patientId, Pageable pageable);

    Page<Meal> findByPatientIdAndMealDatetimeBetween(
            Long patientId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Meal> findByPatientIdAndMealType(Long patientId, MealType mealType, Pageable pageable);

    Page<Meal> findByPatientIdAndMealDatetimeBetweenAndMealType(
            Long patientId, LocalDateTime from, LocalDateTime to, MealType mealType, Pageable pageable);

    Optional<Meal> findByIdAndPatientId(Long id, Long patientId);
}
