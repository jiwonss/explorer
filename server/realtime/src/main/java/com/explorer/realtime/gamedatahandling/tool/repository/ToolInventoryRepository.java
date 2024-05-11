package com.explorer.realtime.gamedatahandling.tool.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ToolInventoryRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "inventoryData:";
    private static final String KEY_SUFFIX = ":tool";

    public ToolInventoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, Long userId, int inventoryIdx, int itemId) {
        String key = KEY_PREFIX + channelId + ":" + userId + KEY_SUFFIX;
        String field = String.valueOf(inventoryIdx);
        String value = String.valueOf(itemId);
        return reactiveHashOperations.put(key, field, value);
    }

    public Mono<Long> delete(String channelId, Long userId, int inventoryIdx) {
        String key = KEY_PREFIX + channelId + ":" + userId + KEY_SUFFIX;
        String field = String.valueOf(inventoryIdx);
        return reactiveHashOperations.remove(key, field);
    }

}
