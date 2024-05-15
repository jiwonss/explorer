package com.explorer.realtime.staticdatahandling.repository.redis;

import com.explorer.realtime.staticdatahandling.dto.ItemInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StaticItemRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "item:";

    public StaticItemRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String category, ItemInfo itemInfo) {
        String key = KEY_PREFIX + category;
        String field = String.valueOf(itemInfo.getId());
        String value = String.valueOf(itemInfo.getCnt());
        return reactiveHashOperations.put(key, field, value);
    }

}
