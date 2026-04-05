package com.bobridze5.TeleMed_backend.api.controllers.user;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API.AUTH_OLD)
@Tag(name = "Аутентификация и авторизация пользователей")
public class AuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентификация и авторизация пользователя"
    )
    public TokenResponse login(@Valid @RequestBody LoginUserRequest request) {
        return userAuthService.login(request);
    }

    @GetMapping("/login")
    @Operation(summary = "Страница логина", description = "Заглушка — возвращает текстовое сообщение")
    public String loginPage() {
        return "Здесь должна быть страница логина";
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновление JWT токена",
            description = "Обновление Access токена и получение новой пары: AccessToken и RefreshToken"
    )
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return userAuthService.refresh(request);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Выход из аккаунта",
            description = "Удаление токенов"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String accessToken) {
        userAuthService.logout(accessToken);
    }

    @GetMapping("/registrationConfirm")
    @Operation(summary = "Подтверждение email", description = "Верификация адреса электронной почты по одноразовому токену из письма. Перенаправляет на страницу входа.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Email подтверждён, редирект на страницу входа"),
            @ApiResponse(responseCode = "400", description = "Токен недействителен или истёк")
    })
    public ResponseEntity<Void> confirmEmail(
            @RequestParam("token") String token
    ) {

        String url = userAuthService.confirmEmail(token);

        return ResponseEntity
                .status(302)
                .header("Location", url)
                .build();
    }

}
