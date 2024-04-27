package com.explorer.realtime.sessionhandling.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;
import reactor.netty.Connection;

@Repository
public class ConnectionRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;

    public ConnectionRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    }

    public void save(String teamCode, Long userId, Connection connection) {
        hashOperations.put(teamCode, String.valueOf(userId), connection.toString());
    }

    public Long count(String teamCode) {
        return redisTemplate.execute(connection -> {
            return connection.hLen(teamCode.getBytes());
        }, true);
    }

}
