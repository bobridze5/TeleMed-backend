package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 64, message = "Пароль должен быть от 8 до 64 символов")
        String password1,

        @NotBlank(message = "Пароль не может быть пустым")
        String password2
) {
    public RegisterAdminRequest {
        if (!password1.equals(password2)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
    }
}
