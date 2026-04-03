package com.bobridze5.TeleMed_backend.core.entity.medical;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_organizations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @Column(name = "organization_name")
    private String name;

    @Column(name = "organization_address")
    private String address;

    @Column(name = "organization_email", unique = true, nullable = false)
    @Email(message = "Email must be correct")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "organization_phone", unique = true, nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_city", referencedColumnName = "city_id")
    private City city;


}
