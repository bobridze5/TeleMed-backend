package com.devops_labs.userService.api.dto.tokens;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must be provided")
        String refreshToken
) {
}
