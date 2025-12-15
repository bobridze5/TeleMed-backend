package com.bobridze5.TeleMed_backend.api.dto.register;

import jakarta.validation.constraints.Email;

public record RegisterUserRequest(
        @Email(message = "Email must be correct")
        String email,
        String password1,
        String password2
) {
}
