package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.api.dto.auth.LoginUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserResponse;
import com.bobridze5.TeleMed_backend.api.dto.tokens.RefreshTokenRequest;
import com.bobridze5.TeleMed_backend.api.dto.tokens.TokenResponse;

public interface UserAuthService {
    RegisterUserResponse register(RegisterUserRequest request, String url);

    TokenResponse login(LoginUserRequest request);

    TokenResponse refresh(RefreshTokenRequest request);

    void logout(String accessToken);

    String confirmEmail(String token);
}
