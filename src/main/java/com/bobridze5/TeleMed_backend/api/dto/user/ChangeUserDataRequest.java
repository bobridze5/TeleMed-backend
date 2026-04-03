package com.bobridze5.TeleMed_backend.api.dto.user;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import jakarta.validation.constraints.Email;

public record ChangeUserDataRequest(
        String firstName,
        String lastName,
        String middleName,
        String nickname,
        @Email(message = "Email must be correct")
        String email,
        String password,
        UserStatus status
) {
}
