package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.api.mappers.UserAuthMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityAlreadyExistsException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.exceptions.InvalidTokenException;
import com.bobridze5.TeleMed_backend.core.exceptions.PasswordsDoNotMatchException;
import com.bobridze5.TeleMed_backend.core.jwt.JwtService;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenStoreServiceImpl refreshTokenStoreService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final UserAuthMapper userAuthMapper;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccessDeniedException("Необходимо подтвердить email");
        }

        if (user.getStatus() == UserStatus.AWAITING_APPROVAL) {
            throw new AccessDeniedException("Ваша заявка находится на проверке у администратора");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AccessDeniedException("Ваша заявка отклонена. Свяжитесь с поддержкой");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccessDeniedException("Аккаунт заблокирован");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new PasswordsDoNotMatchException(HttpStatus.NOT_FOUND, "User login or password incorrect");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenStoreService.save(user.getId(), refreshToken, jwtService.getRefreshLifeTimeMs());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();

        if (!jwtService.isRefreshTokenValid(token)) {
            throw new InvalidTokenException("RefreshToken is invalid");
        }

        Claims claims = jwtService.parse(token);
        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with username = " + email + " not found")
                );

        String actualRefreshToken = refreshTokenStoreService.get(user.getId());
        if (actualRefreshToken == null || !actualRefreshToken.equals(token)) {
            throw new InvalidTokenException("RefreshToken is not recognized");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new TokenResponse(accessToken, token);
    }

    public void logout(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        if (!jwtService.isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("Invalid access Token");
        }

        String email = jwtService.extractEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with username = " + email + " not found")
                );

        refreshTokenStoreService.delete(user.getId());
    }

    @Transactional
    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.getToken(token);

        if (!verificationToken.isValid()) {
            verificationTokenService.deleteToken(token);
            throw new IllegalArgumentException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationTokenService.deleteToken(token);
    }

}
