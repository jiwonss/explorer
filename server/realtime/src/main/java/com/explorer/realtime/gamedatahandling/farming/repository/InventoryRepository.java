package com.explorer.realtime.gamedatahandling.farming.repository;

import com.explorer.realtime.gamedatahandling.farming.dto.ItemInfo;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class InventoryRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "data:";
    private static final String KEY_SUFFIX = ":inventory";

    public InventoryRepository(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Void> save(Long userId, ItemInfo itemInfo) {
        return Mono.when().then();
    }

}
