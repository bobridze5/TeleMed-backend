package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.food.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, Long> {
    Optional<MealItem> findByIdAndMealId(Long id, Long mealId);
}
