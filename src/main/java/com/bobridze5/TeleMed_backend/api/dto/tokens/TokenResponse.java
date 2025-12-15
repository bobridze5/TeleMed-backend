package com.bobridze5.TeleMed_backend.api.dto.tokens;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
