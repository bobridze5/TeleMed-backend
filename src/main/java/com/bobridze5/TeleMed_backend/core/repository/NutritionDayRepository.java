package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.report.food.NutritionDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionDayRepository extends JpaRepository<NutritionDay, Long> {

    Optional<NutritionDay> findByPatientIdAndDiaryDate(Long patientId, LocalDate diaryDate);

    List<NutritionDay> findByPatientIdAndDiaryDateBetweenOrderByDiaryDateAsc(
            Long patientId, LocalDate from, LocalDate to);
}
