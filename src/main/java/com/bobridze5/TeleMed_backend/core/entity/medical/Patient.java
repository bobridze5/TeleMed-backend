package com.bobridze5.TeleMed_backend.core.entity.medical;

import com.bobridze5.TeleMed_backend.core.entity.Ownable;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isIdentifiedBy(Long id) {
        return this.id != null && this.id.equals(id);
    }

    public boolean isOwner(Ownable entity) {
        Patient owner = entity.getPatient();

        return owner != null && this.id.equals(owner.getId());
    }

    public boolean canView(Dish dish) {
        return dish.getPatient() == null || isOwner(dish);
    }
}
