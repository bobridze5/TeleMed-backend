package com.bobridze5.TeleMed_backend.api.dto.data;

import com.bobridze5.TeleMed_backend.core.entity.UserStatus;
import jakarta.validation.constraints.Email;

public record ChangeUserDataResponse(
        long id,
        String firstName,
        String lastName,
        String middleName,
        String nickname,
        @Email(message = "Email must be correct")
        String email,
        UserStatus status
) {
}
