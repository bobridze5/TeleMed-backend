package com.bobridze5.TeleMed_backend.core.service.profile;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Deprecated
public class ProfileFactory {
    private final List<Profile> profiles;


    public Profile getProfile(UserRole role) {
        return profiles.stream()
                .filter(p -> p.getRole() == role)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Профиль для роли не найден"));
    }
}
