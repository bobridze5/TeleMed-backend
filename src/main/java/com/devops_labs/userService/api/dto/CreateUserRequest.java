package com.devops_labs.userService.api.dto;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String password // Дописать passwordHash и тп
) {
}
