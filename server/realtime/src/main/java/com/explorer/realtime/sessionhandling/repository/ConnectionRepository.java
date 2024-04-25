package com.explorer.realtime.sessionhandling.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConnectionRepository {

    private final RedisTemplate<String, String> redisTemplate;

}
