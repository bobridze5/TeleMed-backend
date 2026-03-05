package com.bobridze5.TeleMed_backend.core.entity;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.report.Dish;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    public boolean isOwner(Dish dish) {
        Patient patient = dish.getPatient();

        if (patient == null) {
            return false;
        }

        return this.id.equals(patient.getId());
    }

    public boolean canView(Dish dish) {
        return dish.getPatient() == null || isOwner(dish);
    }
}
