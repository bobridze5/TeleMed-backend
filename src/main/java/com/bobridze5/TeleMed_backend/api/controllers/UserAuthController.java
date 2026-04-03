package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.login.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.register.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.register.RegisterUserResponse;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.core.service.UserAuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/auth")
@Tag(name = "Регистрация, Аутентификация и авторизация")
public class UserAuthController {

    private final UserAuthServiceImpl userAuthService;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать нового пользователя"
    )
    public ResponseEntity<RegisterUserResponse> register(
            @RequestBody RegisterUserRequest request
    ) {
        RegisterUserResponse response = userAuthService.register(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентификация и авторизация пользователя"
    )
    public ResponseEntity<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновление JWT токена",
            description = "Обновление Access токена и получение новой пары: AccessToken и RefreshToken"
    )
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = userAuthService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Выход из аккаунта",
            description = "Удаление токенов"
    )
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        userAuthService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

}
