package com.bobridze5.TeleMed_backend.api.dto.tokens;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must be provided")
        String refreshToken
) {
}
