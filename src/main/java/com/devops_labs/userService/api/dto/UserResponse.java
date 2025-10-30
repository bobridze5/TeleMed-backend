package com.devops_labs.userService.api.dto;

import com.devops_labs.userService.core.entity.UserStatus;

public record UserResponse(
        long id,
        String firstName,
        String lastName,
        String middleName,
        UserStatus userStatus
) {
}
