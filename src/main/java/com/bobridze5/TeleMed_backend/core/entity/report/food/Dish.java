package com.bobridze5.TeleMed_backend.core.entity.report.food;

import com.bobridze5.TeleMed_backend.core.entity.Ownable;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dish")
//@Check(constraints = "dish_calories_per_100 >= 0 " +
//        "AND dish_carbs_per_100 >= 0 " +
//        "AND dish_protein_per_100 >= 0 " +
//        "AND dish_fats_per_100 >= 0")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Ownable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = true)
    private Patient patient;

    @Column(name = "dish_name", nullable = false)
    private String name;

    @PositiveOrZero
    @Column(name = "dish_calories_per_100", nullable = false)
    private Double calories;

    @PositiveOrZero
    @Column(name = "dish_carbs_per_100", nullable = false)
    private Double carbs;

    @PositiveOrZero
    @Column(name = "dish_protein_per_100", nullable = false)
    private Double protein;

    @PositiveOrZero
    @Column(name = "dish_fats_per_100", nullable = false)
    private Double fats;

    // TODO: посмотреть вариант размещения на S3 хранилище, иначе в BLOB формате
    @Column(name = "dish_photo_url", length = 512)
    private String photoURL;

    @Column(name = "dish_description", columnDefinition = "TEXT")
    private String description;
}
