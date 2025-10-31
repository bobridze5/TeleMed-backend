package com.devops_labs.userService.api.dto.register;

public record RegisterUserRequest(
        String email,
        String password1,
        String password2
) {
}
