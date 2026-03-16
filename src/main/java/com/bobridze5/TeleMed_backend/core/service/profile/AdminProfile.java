package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminProfile implements Profile {
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    @Override
    public void createProfile(User user) {
        // TODO: добавить логику для админа
    }

}
