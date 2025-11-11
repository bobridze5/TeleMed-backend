package com.devops_labs.userService.core.service;

import com.devops_labs.userService.api.dto.login.LoginUserRequest;
import com.devops_labs.userService.api.dto.tokens.TokenResponse;
import com.devops_labs.userService.api.dto.tokens.RefreshTokenRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserResponse;
import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.entity.UserStatus;
import com.devops_labs.userService.core.exceptions.*;
import com.devops_labs.userService.core.jwt.JwtService;
import com.devops_labs.userService.core.repository.UserRepository;
import com.devops_labs.userService.core.service.interfaces.UserAuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.authentication.core.TokenRequestException;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenStoreServiceImpl refreshTokenStoreService;

    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {

        if (!request.password1().equals(request.password2())) {
            throw new PasswordsDoNotMatchException();
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException("User with email = " + request.email() + " already exists");
        }

        String hash = getHashPassword(request.password1());


        String username = request.email().split("@")[0];
        User user = User.builder()
                .email(request.email())
                .username(username)
                .nickname(username)
                .passwordHash(hash)
                .status(UserStatus.INACTIVE)
                .build();

        user = userRepository.save(user);

        return new RegisterUserResponse(user.getId());
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        String email = request.email();
        String username = request.username();
        String password = request.password();

        if (email == null || email.isBlank()) {
            if (username == null || username.isBlank()) {
                throw new MissingRequestBodyFieldException("Either email or username must be provided");
            }
        } else {
            username = email.split("@")[0];
        }

        if (password == null || password.isBlank()) {
            throw new MissingRequestBodyFieldException("Password must be provided");
        }


        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found")
                );

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new PasswordsDoNotMatchException(HttpStatus.NOT_FOUND, "User login or password incorrect");
        }

        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

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
        String username = claims.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with username = " + username + " not found")
                );

        String actualRefreshToken = refreshTokenStoreService.get(user.getId());
        if (actualRefreshToken == null || !actualRefreshToken.equals(token)) {
            throw new InvalidTokenException("RefreshToken is not recognized");
        }

        String accessToken = jwtService.generateAccessToken(user);
//        String refreshToken = jwtService.generateRefreshToken(user);

//        refreshTokenStoreService.save(user.getId(), refreshToken, jwtService.getRefreshLifeTimeMs());

        return new TokenResponse(accessToken, token);
    }

    public void logout(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        if (!jwtService.isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("Invalid access Token");
        }

        String username = jwtService.extractUsername(accessToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with username = " + username + " not found")
                );

        refreshTokenStoreService.delete(user.getId());
    }

    private String getHashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
