package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthMapperImpl implements UserAuthMapper {
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public User mapToEntity(RegisterUserRequest request) {
        return User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password1()))
                .status(UserStatus.PENDING)
                .build();
    }

    @Override
    public RegisterUserResponse mapToResponse(User entity) {
        return null;
    }
}
