package com.explorer.apigateway.global.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class BlackListRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "blackList::";

    public void save(String accessToken, Date expirationDate) {
        String key = KEY_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, String.valueOf(expirationDate));
        redisTemplate.expire(key, expirationDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public boolean exist(String accessToken) {
        return redisTemplate.hasKey(KEY_PREFIX + accessToken);
    }

}
