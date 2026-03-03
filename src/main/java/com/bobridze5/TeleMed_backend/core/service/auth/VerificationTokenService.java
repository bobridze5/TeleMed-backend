package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.VerificationToken;

import java.time.LocalDateTime;

public interface VerificationTokenService {
    int EXPIRATION = 24 * 60;

    VerificationToken createToken(User user);
    void deleteToken(String token);
    VerificationToken getToken(String token);

    default LocalDateTime calculateExpiryDate(){
       return LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    static int getExpirationMinutes(){
        return EXPIRATION;
    }

}
