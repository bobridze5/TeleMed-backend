package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL) // TODO: Подумать
    @JoinColumn(name = "record_id", referencedColumnName = "record_id")
    private MedicalRecord record;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    private Patient patient;
}
