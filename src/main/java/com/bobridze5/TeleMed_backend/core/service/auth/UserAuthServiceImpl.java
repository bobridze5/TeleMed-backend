package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserResponse;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.api.mappers.UserAuthMapper;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;
import com.bobridze5.TeleMed_backend.core.exceptions.*;
import com.bobridze5.TeleMed_backend.core.jwt.JwtService;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.service.profile.Profile;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenStoreServiceImpl refreshTokenStoreService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final UserAuthMapper userAuthMapper;
    private final Map<UserRole, Profile> profileMap;

    @Override
    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request, String url) {
        log.info("Начало регистрации: email = {}, role = {}", request.email(), request.role());

        if (!request.password1().equals(request.password2())) {
            log.warn("Ошибка регистрации: пароли не совпали для email = {}", request.email());
            throw new PasswordsDoNotMatchException();
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Ошибка регистрации: email = {} уже существует", request.email());
            throw new EntityAlreadyExistsException("User with email = " + request.email() + " already exists");
        }

        User user = userRepository.save(userAuthMapper.mapToEntity(request));

        Optional.ofNullable(profileMap.get(request.role()))
                .orElseThrow(() -> new IllegalArgumentException("Роли не существует"))
                .createProfile(user);


        var verificationToken = verificationTokenService.createToken(user);
        emailService.sendVerificationToken(user.getEmail(), url, verificationToken.getToken());

        log.info("Пользователь с id = {} зарегистрирован", user.getId());
        return new RegisterUserResponse(user.getId());
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // TODO: add Exception
        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccessDeniedException("Need to confirm registration");
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
    public String confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.getToken(token);

        if (!verificationToken.isValid()) {
            // TODO: TOKEN EXCEPTION
            verificationTokenService.deleteToken(token);
            throw new IllegalArgumentException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // TODO: URL
        verificationTokenService.deleteToken(token);

        return "http://localhost:8083/api/v1/users/auth/login";
    }

}
