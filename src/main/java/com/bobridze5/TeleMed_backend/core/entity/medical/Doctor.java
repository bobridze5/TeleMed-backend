package com.bobridze5.TeleMed_backend.core.entity.medical;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_user_id", nullable = false, referencedColumnName = "user_id")
    private User user;

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
