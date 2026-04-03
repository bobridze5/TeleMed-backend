package com.bobridze5.TeleMed_backend.core.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected Long id;

    @Column(name = "user_first_name", length = 120)
    protected String firstName;

    @Column(name = "user_last_name", length = 120)
    protected String lastName;

    @Column(name = "user_middle_name", length = 120)
    protected String middleName;

    @Column(name = "user_date_of_birth")
    protected LocalDate dateOfBirth;

    @Column(name = "user_gender", length = 1)
    protected Character gender;

    @Column(name = "user_email", unique = true, nullable = false)
    @Email(message = "Email must be correct")
    @NotBlank(message = "Email is mandatory")
    protected String email;

    @Column(name = "user_password_hash", nullable = false)
    @NotBlank(message = "password is mandatory")
    protected String passwordHash;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    protected UserStatus status;

    @CreationTimestamp
    @Column(name = "user_datetime_created", nullable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "user_datetime_updated", nullable = false)
    protected LocalDateTime updatedAt;

    public String getFullName() {
        StringBuilder name = new StringBuilder();
        if (lastName != null && !lastName.isBlank()) {
            name.append(lastName);
        }
        if (firstName != null && !firstName.isBlank()) {
            if (!name.isEmpty()) name.append(" ");
            name.append(firstName);
        }
        if (middleName != null && !middleName.isBlank()) {
            if (!name.isEmpty()) name.append(" ");
            name.append(middleName);
        }
        return name.toString();
    }
}
