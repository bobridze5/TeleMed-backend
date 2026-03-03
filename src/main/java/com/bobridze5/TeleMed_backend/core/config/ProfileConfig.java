package com.bobridze5.TeleMed_backend.core.config;

import com.bobridze5.TeleMed_backend.core.entity.auth.UserRole;
import com.bobridze5.TeleMed_backend.core.service.profile.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ProfileConfig {
    @Bean
    public Map<UserRole, Profile> profileMap(List<Profile> profiles) {
        return profiles.stream()
                .collect(Collectors.toMap(Profile::getRole, Function.identity()));
    }
}
