package com.bobridze5.TeleMed_backend.core.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenStoreServiceImpl implements RedisStoreService<String, String> {
    private static final String PREFIX = "password_reset:";

    private final RedisTemplate<String, String> template;

    @Override
    public void save(String token, String email, long ttlMinutes) {
        template.opsForValue().set(getKey(token), email, ttlMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String get(String token) {
        return template.opsForValue().get(getKey(token));
    }

    @Override
    public void delete(String token) {
        template.delete(getKey(token));
    }

    private String getKey(String token) {
        return PREFIX + token;
    }
}
