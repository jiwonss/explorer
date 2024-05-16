package com.explorer.realtime.staticdatahandling.repository.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class InvalidInventoryItemCategoryRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "invalidInventoryItemCategory";

    public InvalidInventoryItemCategoryRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Long> save(String category) {
        return reactiveRedisTemplate.opsForSet().add(KEY_PREFIX, category);
    }


}
