package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email")
        String email,

        @Size(min = 8, max = 64, message = "Пароль должен быть от 8 до 64 символов")
        @NotBlank
        String password1,

        @NotBlank
        String password2
) {
}
