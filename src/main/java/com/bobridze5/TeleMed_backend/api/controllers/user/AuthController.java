package com.bobridze5.TeleMed_backend.api.controllers.user;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.auth.ForgotPasswordRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.ResetPasswordRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.AuthService;
import com.bobridze5.TeleMed_backend.core.service.auth.PasswordResetService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API.AUTH)
@Tag(name = "Аутентификация и авторизация пользователей")
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UrlBuilder urlBuilder;

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентификация и авторизация пользователя"
    )
    public TokenResponse login(@Valid @RequestBody LoginUserRequest request) {
        return authService.login(request);
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
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Выход из аккаунта",
            description = "Удаление токенов"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
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
        String url = authService.confirmEmail(token);

        return ResponseEntity
                .status(302)
                .header("Location", url)
                .build();
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Запрос сброса пароля",
            description = "Отправляет письмо со ссылкой для сброса пароля на указанный email. Действует 30 минут."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Письмо отправлено"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким email не найден")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest servletRequest
    ) {
        String url = urlBuilder.buildAbsoluteUrl(servletRequest, API.AUTH).toString();
        passwordResetService.sendResetPasswordEmail(request.email(), url);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Сброс пароля",
            description = "Устанавливает новый пароль по токену из письма."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пароль успешно изменён"),
            @ApiResponse(responseCode = "404", description = "Токен недействителен или истёк")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
    }
}
