package com.bobridze5.TeleMed_backend.core.entity.medical;

import com.bobridze5.TeleMed_backend.core.entity.Ownable;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.report.food.Dish;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patients")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "patient_id")
public class Patient extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_diabetes_type")
    private DiabetesType diabetesType;

    @Column(name = "patient_diagnosis_date")
    private LocalDate diagnosisDate;

    @Builder.Default
    @Column(name = "patient_insulin_dependency", nullable = false)
    private Boolean isInsulinDependency = false;

    @Builder.Default
    @Column(name = "patient_target_low", nullable = false)
    private Double targetLow = 3.9;

    @Builder.Default
    @Column(name = "patient_target_high", nullable = false)
    private Double targetHigh = 10.0;

    @Column(name = "patient_blood_type", length = 10)
    private String bloodType;

    @Column(name = "patient_height_cm")
    private Integer heightCm;

    @Column(name = "patient_hba1c")
    private Double hba1c;

    // Дневные нутри-цели. Все nullable: если не заданы — дневник просто
    // отображает текущие totals без сравнения с нормой.
    @Column(name = "patient_daily_calories_goal")
    private Integer dailyCaloriesGoal;

    @Column(name = "patient_daily_protein_goal")
    private Double dailyProteinGoal;

    @Column(name = "patient_daily_fats_goal")
    private Double dailyFatsGoal;

    @Column(name = "patient_daily_carbs_goal")
    private Double dailyCarbsGoal;

    @Override
    public String getRole() {
        return "PATIENT";
    }

    public boolean isOwner(Ownable entity) {
        Patient owner = entity.getPatient();

        return owner != null && this.id.equals(owner.getId());
    }

    public boolean canView(Dish dish) {
        return dish.getPatient() == null || isOwner(dish);
    }
}
