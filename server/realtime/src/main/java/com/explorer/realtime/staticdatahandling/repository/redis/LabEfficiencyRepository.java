package com.explorer.realtime.staticdatahandling.repository.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LabEfficiencyRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "LabEfficiency:";

    public LabEfficiencyRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> save(int level, float efficiency) {
        String key = KEY_PREFIX + String.valueOf(level);
        String value = String.valueOf(efficiency);
        return reactiveRedisTemplate.opsForValue().set(key, value);
    }

}
