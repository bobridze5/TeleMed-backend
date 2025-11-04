package com.devops_labs.userService.api.dto.data;

import com.devops_labs.userService.core.entity.UserStatus;

public record ChangeUserDataResponse(
        long id,
        String firstName,
        String lastName,
        String middleName,
        String nickname,
        String email,
        UserStatus status
) {
}
