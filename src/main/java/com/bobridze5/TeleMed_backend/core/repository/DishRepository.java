package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.eat.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    @Query("SELECT d FROM Dish d WHERE d.id = :dishId AND (d.patient IS NULL OR d.patient.id = :patientId)")
    Optional<Dish> findDish(@Param("dishId") Long dishId, @Param("patientId") Long patientId);
}
