package com.bobridze5.TeleMed_backend.api.dto.users;

import com.bobridze5.TeleMed_backend.core.entity.UserStatus;

public record UserResponse(
        long id,
        String firstName,
        String lastName,
        String middleName,
        UserStatus userStatus
) {
}
