package com.devops_labs.userService.api.dto.data;

import com.devops_labs.userService.core.entity.UserStatus;
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
