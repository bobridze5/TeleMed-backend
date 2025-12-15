package com.bobridze5.TeleMed_backend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "date_expiration")
    private LocalDateTime expiryDate;

//    private Date calculateExpiryDate() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
//        calendar.add(Calendar.MINUTE, EXPIRATION);
//        return new Date(calendar.getTime().getTime());
//    }
}
