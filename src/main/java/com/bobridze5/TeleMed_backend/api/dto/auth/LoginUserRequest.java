package com.bobridze5.TeleMed_backend.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @NotBlank
        @Email(message = "Email must be correct")
        String email,

        @NotBlank
        String password
) {
}
