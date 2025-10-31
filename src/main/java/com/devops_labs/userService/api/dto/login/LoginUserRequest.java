package com.devops_labs.userService.api.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        String email,
        String username,
        @NotBlank String password
        ) {
}
