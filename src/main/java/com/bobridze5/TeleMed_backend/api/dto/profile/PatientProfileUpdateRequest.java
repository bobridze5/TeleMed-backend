package com.bobridze5.TeleMed_backend.api.dto.profile;

import com.bobridze5.TeleMed_backend.core.entity.medical.DiabetesType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProfileUpdateRequest extends UserProfileUpdateRequest {
    private DiabetesType diabetesType;

    @PastOrPresent(message = "Дата может не может быть в будущем")
    private LocalDate diagnosisDate;

    private Boolean isInsulinDependency;

    @Positive(message = "Нижняя граница глюкозы должна быть положительной")
    private Double targetLow;

    @Positive(message = "Верхняя граница глюкозы должна быть положительной")
    private Double targetHigh;

    private String bloodType;

    @Positive(message = "Рост должен быть положительным числом")
    private Integer heightCm;

    @Positive(message = "HbA1c должен быть положительным числом")
    private Double hba1c;

    @Positive(message = "Норма калорий должна быть положительным числом")
    private Integer dailyCaloriesGoal;

    @Positive(message = "Норма белков должна быть положительным числом")
    private Double dailyProteinGoal;

    @Positive(message = "Норма жиров должна быть положительным числом")
    private Double dailyFatsGoal;

    @Positive(message = "Норма углеводов должна быть положительным числом")
    private Double dailyCarbsGoal;

    /**
     * Инвариант: если пациент задаёт обе границы целевого диапазона глюкозы
     * одним PATCH'ем, верхняя должна быть строго больше нижней. Если
     * передана только одна (или ни одной) — пропускаем проверку, потому что
     * сравнение с уже сохранённым значением требует обращения к БД и
     * выходит за рамки бин-валидации.
     *
     * Возврат {@code true} означает «инвариант выполняется». Имя геттера
     * {@code isTargetRangeValid} → Hibernate Validator повесит ошибку на
     * виртуальное поле {@code targetRangeValid} в fieldErrors; этого
     * достаточно для top-level message, но фронт уже делает ту же
     * проверку и вешает понятную ошибку конкретно на targetHigh.
     */
    @AssertTrue(message = "Верхняя граница должна быть больше нижней")
    @JsonIgnore
    public boolean isTargetRangeValid() {
        if (targetLow == null || targetHigh == null) return true;
        return targetHigh > targetLow;
    }
}