package com.devops_labs.userService.core.service;

import com.devops_labs.userService.api.dto.login.LoginUserRequest;
import com.devops_labs.userService.api.dto.login.LoginUserResponse;
import com.devops_labs.userService.api.dto.register.RegisterUserRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserResponse;
import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.entity.UserStatus;
import com.devops_labs.userService.core.jwt.JwtService;
import com.devops_labs.userService.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {

        if (!request.password1().equals(request.password2())) {
            throw new IllegalArgumentException("The passwords don't match");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with email = " + request.email() + " already exists");
        }

        String hash = getHashPassword(request.password1());

        User user = User.builder()
                .email(request.email())
                .username(request.email().split("@")[0])
                .passwordHash(hash)
                .status(UserStatus.INACTIVE)
                .build();

        user = userRepository.save(user);

        return new RegisterUserResponse(user.getId());
    }

    @Transactional
    public LoginUserResponse login(LoginUserRequest request) {
        var email = request.email();
        var username = request.username();
        var password = request.password();

        if (email == null || email.isBlank()) {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Either email or username must be provided");
            }
        } else {
            username = email.split("@")[0];
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must be provided");
        }

        String hash = getHashPassword(password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User doesn't exists"));

//        log.info(hash);
//        log.info(user.getPasswordHash());
//        log.info(user.getPassword());
//        log.info(String.valueOf(hash.equals(user.getPasswordHash())));


        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Passwords don't match");
        }


        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginUserResponse(accessToken, refreshToken);
    }


    private String getHashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
