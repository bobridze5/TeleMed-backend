package com.bobridze5.TeleMed_backend.core.entity.auth;

import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "verification_tokens")
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "date_expiration")
    private LocalDateTime expiryDate;

    public boolean isValid() {
        return expiryDate != null && !this.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
