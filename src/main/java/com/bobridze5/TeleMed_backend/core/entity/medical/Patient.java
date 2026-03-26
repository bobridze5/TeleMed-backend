package com.bobridze5.TeleMed_backend.core.entity.medical;

import com.bobridze5.TeleMed_backend.core.entity.Ownable;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
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

    public boolean isOwner(Ownable entity) {
        Patient owner = entity.getPatient();

        return owner != null && this.id.equals(owner.getId());
    }

    public boolean canView(Dish dish) {
        return dish.getPatient() == null || isOwner(dish);
    }
}
