package com.bobridze5.TeleMed_backend.api.controllers;

import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserResponse;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;
import com.bobridze5.TeleMed_backend.core.service.auth.UserAuthService;
import com.bobridze5.TeleMed_backend.core.service.utils.UrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(API.AUTH)
@Tag(name = "Регистрация, Аутентификация и авторизация")
public class UserAuthController {
    private final UrlBuilder urlBuilder;
    private final UserAuthService userAuthService;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать нового пользователя"
    )
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletRequest servletRequest
    ) {
        URI authBase = urlBuilder.buildAbsoluteUrl(servletRequest, API.AUTH);

        RegisterUserResponse response = userAuthService.register(request, authBase.toString());
        URI location = urlBuilder.buildAbsoluteUrl(servletRequest, API.USERS + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентификация и авторизация пользователя"
    )
    public TokenResponse login(@Valid @RequestBody LoginUserRequest request) {
        return userAuthService.login(request);
    }

    @GetMapping("/login")
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
