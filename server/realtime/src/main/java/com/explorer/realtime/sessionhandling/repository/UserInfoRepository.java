package com.explorer.realtime.sessionhandling.repository;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository {

    private HashOperations<String, String, Object> hashOperations;

    private static final String KEY_PREFIX = "user:";

    public UserInfoRepository(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    }

    public void save(UserInfo userInfo) {
        hashOperations.put(KEY_PREFIX + String.valueOf(userInfo.getUserId()), "nickname", userInfo.getNickname());
        hashOperations.put(KEY_PREFIX + String.valueOf(userInfo.getUserId()), "avatar", String.valueOf(userInfo.getAvatar()));
    }

}
