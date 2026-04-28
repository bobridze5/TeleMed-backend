package com.bobridze5.TeleMed_backend.core.entity.report.food;

import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;


@Entity
@Table(name = "meal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ToString.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_day_id")
    @ToString.Exclude
    private NutritionDay nutritionDay;

    @Column(name = "meal_datetime")
    private LocalDateTime mealDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<MealItem> items = new ArrayList<>();

    @Transient
    public double totalCalories() {
        return sum(MealItem::totalCalories);
    }

    @Transient
    public double totalCarbs() {
        return sum(MealItem::totalCarbs);
    }

    @Transient
    public double totalProtein() {
        return sum(MealItem::totalProtein);
    }

    @Transient
    public double totalFats() {
        return sum(MealItem::totalFats);
    }

    private double sum(ToDoubleFunction<MealItem> function) {
        if (items == null) return 0.0;
        return items.stream()
                .filter(java.util.Objects::nonNull)
                .mapToDouble(function)
                .sum();
    }
}
