package com.explorer.realtime.staticdatahandling.repository.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class PositionRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;


    private static final String KEY_PREFIX = "position:";

    public PositionRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Long> save(int mapId, String position) {
        String key = KEY_PREFIX + String.valueOf(mapId);
        return reactiveRedisTemplate.opsForList().rightPush(key, position);
    }

}
