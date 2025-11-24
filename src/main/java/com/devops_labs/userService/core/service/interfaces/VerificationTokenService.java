package com.devops_labs.userService.core.service.interfaces;

import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.entity.VerificationToken;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

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
