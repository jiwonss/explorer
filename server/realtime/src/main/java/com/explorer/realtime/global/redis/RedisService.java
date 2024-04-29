package com.explorer.realtime.global.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class RedisService {

    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public RedisService(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public Mono<Boolean> saveUidToTeamCode(String teamCode, String uid, String connectionInfo) {
        return redisOperations.opsForHash().put(teamCode, uid, connectionInfo);
    }

    public Mono<Map<Object, Object>> readUidsFromTeamCode(String teamCode) {
        return redisOperations.opsForHash().entries(teamCode).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

}
