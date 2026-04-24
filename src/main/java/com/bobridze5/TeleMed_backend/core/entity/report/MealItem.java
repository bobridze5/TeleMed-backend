package com.bobridze5.TeleMed_backend.core.entity.report;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "meal_item")
@Check(constraints = "meal_item_portion_grams >= 0")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @PositiveOrZero
    @Column(name = "meal_item_portion_grams", nullable = false)
    private Double portionGrams;

    @Column(name = "meal_item_notes", columnDefinition = "TEXT")
    private String notes;

}
