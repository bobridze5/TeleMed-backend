package com.bobridze5.TeleMed_backend.core.entity.medical;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "doctor_id")
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_specialization_id", referencedColumnName = "specialization_id")
    private Specialization specialization;

    @Column(name = "doctor_experience")
    private Integer experience;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_organization_id", referencedColumnName = "organization_id")
    private MedicalOrganization organization;

    @Column(name = "doctor_qualification")
    private String qualification;
}
