package com.bobridze5.TeleMed_backend.core.service.interfaces;

import com.bobridze5.TeleMed_backend.core.entity.User;
import com.bobridze5.TeleMed_backend.core.entity.VerificationToken;

import java.time.LocalDateTime;

public interface VerificationTokenService {
    int EXPIRATION = 24 * 60;

    void createToken(User user, String token);
    void deleteToken(String token);
    VerificationToken getToken(String token);
    boolean isTokenValid(String token);

    default LocalDateTime calculateExpiryDate(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
//        calendar.add(Calendar.MINUTE, EXPIRATION);
//        return new Date(calendar.getTime().getTime());
       return LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    static int getExpirationMinutes(){
        return EXPIRATION;
    }

}
