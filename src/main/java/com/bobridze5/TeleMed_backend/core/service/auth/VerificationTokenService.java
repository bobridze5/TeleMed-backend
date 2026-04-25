package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private static final int EXPIRATION_MINUTES = 24 * 60;

    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public VerificationToken createToken(User user) {
        log.info("Создание токена верификации для user_id = {}", user.getId());
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(calculateExpiryDate())
                .build();

        log.info("Сохранение токена верификации: token = {}", verificationToken.getToken());
        return verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void deleteToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
        log.info("Удаление токена верификации: token = {}", verificationToken.getToken());
        verificationTokenRepository.delete(verificationToken);
    }

    public VerificationToken getToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }
}
