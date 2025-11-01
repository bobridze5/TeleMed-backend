package com.devops_labs.userService.api.dto.tokens;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
