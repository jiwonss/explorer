package com.explorer.realtime.staticdatahandling.repository.redis;

import com.explorer.realtime.staticdatahandling.dto.DroppedItemInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class DroppedItemRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "farm:";

    public DroppedItemRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String category, int id, DroppedItemInfo droppedItemInfo) {
        String key = KEY_PREFIX + category + ":" + String.valueOf(id);
        String field = droppedItemInfo.getCategory() + ":" + String.valueOf(droppedItemInfo.getId());
        String value = String.valueOf(droppedItemInfo.getCnt());
        return reactiveHashOperations.put(key, field, value);
    }

}
