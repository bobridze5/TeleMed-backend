package com.devops_labs.userService.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "users_first_name", length = 120, nullable = false)
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @Column(name = "users_last_name", length = 120, nullable = false)
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Column(name = "users_middle_name", length = 120)
    private String middleName;

    @Column(name = "users_email", unique = true)
    @Email(message = "Email must be correct")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "users_password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "users_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "users_datetime_created", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "users_datetime_updated", nullable = false)
    private LocalDateTime updatedAt;

}
