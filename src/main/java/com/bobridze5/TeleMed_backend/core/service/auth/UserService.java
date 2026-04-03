package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.profile.UserProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;

public interface UserService {
    User updateProfile(User user, UserProfileUpdateRequest request);
}
