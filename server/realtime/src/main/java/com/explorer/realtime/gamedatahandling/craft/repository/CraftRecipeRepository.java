package com.explorer.realtime.gamedatahandling.craft.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class CraftRecipeRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "craft:";

    public CraftRecipeRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Map<Object, Object>> find(String itemCategory, int itemId) {
        String key = KEY_PREFIX + itemCategory + ":" + String.valueOf(itemId);
        return reactiveHashOperations.entries(key)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

}
