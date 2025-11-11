package com.devops_labs.userService.api.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @Email(message = "Email must be correct")
        String email,
        String username,
        @NotBlank String password
        ) {
}
