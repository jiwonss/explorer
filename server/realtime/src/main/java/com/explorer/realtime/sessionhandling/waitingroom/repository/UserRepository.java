package com.explorer.realtime.sessionhandling.waitingroom.repository;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    private static final String KEY_PREFIX = "user:";

    public UserRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    }

    public void save(UserInfo userInfo) {
        hashOperations.put(KEY_PREFIX + userInfo.getUserId(), "nickname", userInfo.getNickname());
        hashOperations.put(KEY_PREFIX + userInfo.getUserId(), "avatar", String.valueOf(userInfo.getAvatar()));
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

}
