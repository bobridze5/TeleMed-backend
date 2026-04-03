package com.bobridze5.TeleMed_backend.core.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenStoreServiceImpl implements RedisStoreService<Long, String> {
    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> template;

    @Override
    public void save(Long userId, String refreshToken, long timeToExpire){
        template.opsForValue()
                .set(getKey(userId), refreshToken, timeToExpire, TimeUnit.MINUTES);
    }

    @Override
    public String get(Long userId){
        return template.opsForValue().get(getKey(userId));
    }

    @Override
    public void delete(Long userId){
        template.delete(getKey(userId));
    }

    private String getKey(long userId){
        return PREFIX + userId;
    }
}
