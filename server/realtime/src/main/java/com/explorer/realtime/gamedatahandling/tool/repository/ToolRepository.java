package com.explorer.realtime.gamedatahandling.tool.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ToolRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "tool:";

    public ToolRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> save(String channelId, Long userId, int inventoryIdx, int itemId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        String value = String.valueOf(inventoryIdx) + ":" + String.valueOf(itemId);
        return reactiveRedisTemplate.opsForValue().set(key, value);
    }

    public Mono<Boolean> delete(String channelId, Long userId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveRedisTemplate.opsForValue().delete(key);
    }

    public Mono<Object> find(String channelId, Long userId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveRedisTemplate.opsForValue().get(key);
    }

}
