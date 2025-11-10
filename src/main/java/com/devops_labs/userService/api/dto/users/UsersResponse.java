package com.devops_labs.userService.api.dto.users;

import java.util.List;

public record UsersResponse(
        List<UserResponse> users
) {
}
