package com.bobridze5.TeleMed_backend.api.dto;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String password
) {
}
