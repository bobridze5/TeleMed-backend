package com.devops_labs.userService.core.service;

import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.entity.VerificationToken;
import com.devops_labs.userService.core.exceptions.EntityNotFoundException;
import com.devops_labs.userService.core.repository.VerificationTokenRepository;
import com.devops_labs.userService.core.service.interfaces.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public void createToken(User user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpiryDate())
                .build();

        verificationTokenRepository.save(verificationToken);
    }

    // TODO: exception
    @Override
    public void deleteToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new EntityNotFoundException("Token not found"));
        verificationTokenRepository.delete(verificationToken);
    }


    // TODO: exception
    @Override
    public VerificationToken getToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
    }

    // TODO: Exception
    @Override
    public boolean isTokenValid(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            return false;
        }

        return true;
    }


}
