package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "specializations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id")
    private Long id;

    @Column(name = "specialization_name", nullable = false, unique = true)
    private String name;

    @Column(name = "specialization_description", length = 500)
    private String description;
}
