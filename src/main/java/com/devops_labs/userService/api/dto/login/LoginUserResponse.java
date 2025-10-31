package com.devops_labs.userService.api.dto.login;

public record LoginUserResponse(
        String accessToken,
        String refreshToken
) {
}
