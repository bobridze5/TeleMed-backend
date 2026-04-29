package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterPatientRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email")
        String email,

        @Size(min = 8, max = 64, message = "Пароль должен быть от 8 до 64 символов")
        @NotBlank
        String password1,

        @Size(min = 8, max = 64, message = "Пароль должен быть от 8 до 64 символов")
        @NotBlank
        String password2,

        String timeZone
) {
    public RegisterPatientRequest {
        if (!password1.equals(password2)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
    }
}
