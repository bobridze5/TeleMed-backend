package com.bobridze5.TeleMed_backend.api.dto.users;

import java.util.List;

public record UsersResponse(
        List<UserResponse> users
) {
}
