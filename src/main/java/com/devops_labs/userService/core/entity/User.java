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
    @Column(name = "users_id")
    private Long id;

    @Column(name = "users_first_name", length = 120)
    private String firstName;

    @Column(name = "users_last_name", length = 120)
    private String lastName;

    @Column(name = "users_middle_name", length = 120)
    private String middleName;

    @Column(name = "users_username", length = 60, unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(name = "users_nickname", length = 60, unique = true)
    @NotBlank(message = "Nickname is mandatory")
    private String nickname;

    @Column(name = "users_email", unique = true, nullable = false)
    @Email(message = "Email must be correct")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "users_password_hash", nullable = false)
    @NotBlank(message = "password is mandatory")
    private String passwordHash;

    @Builder.Default
    @Column(name = "users_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.INACTIVE;

    @CreationTimestamp
    @Column(name = "users_datetime_created", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "users_datetime_updated", nullable = false)
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
        return username;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }
}
