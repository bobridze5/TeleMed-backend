package com.bobridze5.TeleMed_backend.core.entity;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_user_id", nullable = false, referencedColumnName = "user_id")
    private User user;

    @Column(name = "doctor_specialization")
    private String specialization;

    @Column(name = "doctor_experience")
    private Integer experience;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_organization", referencedColumnName = "organization_id")
    private MedicalOrganization organization;

    @Column(name = "doctor_qualification")
    private String qualification;
}
