package com.bobridze5.TeleMed_backend.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_first_name", length = 120)
    private String firstName;

    @Column(name = "user_last_name", length = 120)
    private String lastName;

    @Column(name = "user_middle_name", length = 120)
    private String middleName;

    @Column(name = "user_email", unique = true, nullable = false)
    @Email(message = "Email must be correct")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "user_password_hash", nullable = false)
    @NotBlank(message = "password is mandatory")
    private String passwordHash;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "user_datetime_created", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "user_datetime_updated", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }
}
