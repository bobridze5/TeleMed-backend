package com.bobridze5.TeleMed_backend.core.entity.report.eat;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "nutrition_day",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_patient_day",
                        columnNames = {"patient_id", "diary_date"}
                )
        },
        indexes = {
                @Index(name = "idx_patient_day", columnList = "patient_id,diary_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="nutrition_day_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id", nullable=false)
    private Patient patient;

    @Column(name="diary_date", nullable=false)
    private LocalDate diaryDate;

    @Column(name="total_calories")
    private Double totalCalories;

    @Column(name="total_carbs")
    private Double totalCarbs;

    @Column(name="total_protein")
    private Double totalProtein;

    @Column(name="total_fats")
    private Double totalFats;

    @Column(columnDefinition="TEXT")
    private String notes;

    private Boolean completed;

    @OneToMany(
            mappedBy="nutritionDay",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();

}
