package com.bobridze5.TeleMed_backend.api.mappers.profile;

import com.bobridze5.TeleMed_backend.api.dto.profile.UserProfileUpdateRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public void update(User user, UserProfileUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getMiddleName() != null) user.setMiddleName(request.getMiddleName());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateBirth() != null) user.setDateOfBirth(request.getDateBirth());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
    }
}
