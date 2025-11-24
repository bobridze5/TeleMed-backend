package com.devops_labs.userService.api.controllers;

import com.devops_labs.userService.api.dto.login.LoginUserRequest;
import com.devops_labs.userService.api.dto.tokens.TokenResponse;
import com.devops_labs.userService.api.dto.tokens.RefreshTokenRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserRequest;
import com.devops_labs.userService.api.dto.register.RegisterUserResponse;
import com.devops_labs.userService.core.service.EmailVerificationTokenServiceImpl;
import com.devops_labs.userService.core.service.UserAuthServiceImpl;
import com.devops_labs.userService.core.service.interfaces.VerificationTokenService;
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
@RequestMapping("/api/v1/users/auth")
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
        String appURL = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/users/auth/")
                .build()
                .toString();

        RegisterUserResponse response = userAuthService.register(request, appURL);
        URI location = URI.create(appURL + response.id());

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

    @GetMapping("/login")
    public ResponseEntity<String> loginPage(){
        return ResponseEntity.ok("Здесь должна быть страница логина");
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

    @GetMapping("/registrationConfirm")
    public ResponseEntity<Void> confirmEmail(
//            @RequestHeader("Authorization") String accessToken,
            @RequestParam("token") String token
    ) {

        String url = userAuthService.confirmEmail(token);

        return ResponseEntity
                .status(302)
                .header("Location", url)
                .build();
    }

}
