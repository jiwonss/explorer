package com.explorer.realtime.gamedatahandling.farming.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ItemRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "item:";

    public ItemRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String itemCategory, int itemId, int maxCnt) {
        String key = KEY_PREFIX + itemCategory;
        String field = String.valueOf(itemId);
        String value = String.valueOf(maxCnt);
        return reactiveHashOperations.put(key, field, value);
    }

    public Mono<Object> findByItemCategoryAndItemId(String itemCategory, int itemId) {
        String key = KEY_PREFIX + itemCategory;
        String field = String.valueOf(itemId);
        return reactiveHashOperations.get(key, field);
    }

}
