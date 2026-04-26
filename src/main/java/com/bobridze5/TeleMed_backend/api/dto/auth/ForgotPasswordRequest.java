package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank
        @Email(message = "Невалидный email")
        String email
) {
}
