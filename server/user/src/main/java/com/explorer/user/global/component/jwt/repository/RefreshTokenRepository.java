package com.explorer.user.global.component.jwt.repository;

import com.explorer.user.global.component.jwt.JwtProps;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProps jwtProps;

    private static final String KEY_PREFIX = "refreshToken::";

    public void save(String playerId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + playerId, refreshToken, jwtProps.refreshExpiration());
    }

    public Optional<String> find(String playerId) {
        String token = redisTemplate.opsForValue().get(KEY_PREFIX + playerId);

        return Optional.ofNullable(token);
    }

    public void delete(String playerId) {
        redisTemplate.delete(KEY_PREFIX + playerId);
    }

}