package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
        String newPassword
) {
}
