package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final long TTL_MINUTES = 30;

    private final UserRepository userRepository;
    private final RedisStoreService<String, String> passwordResetTokenStore;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void sendResetPasswordEmail(String email, String url) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        String token = UUID.randomUUID().toString();
        passwordResetTokenStore.save(token, email, TTL_MINUTES);
        emailService.sendPasswordResetEmail(email, url, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        String email = passwordResetTokenStore.get(token);
        if (email == null) {
            throw new EntityNotFoundException("Токен недействителен или истёк");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenStore.delete(token);
    }
}
