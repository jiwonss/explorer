package com.explorer.realtime.staticdatahandling.repository.redis;

import com.explorer.realtime.staticdatahandling.dto.AvailableInventoryItemInfo;
import com.explorer.realtime.staticdatahandling.dto.DroppedItemInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class AvailableInventoryItemRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "availableInventoryItem";

    public AvailableInventoryItemRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Long> save(AvailableInventoryItemInfo availableInventoryItemInfo) {
        String itemInfo = availableInventoryItemInfo.getCategory() + ":" + String.valueOf(availableInventoryItemInfo.getId());
        return reactiveRedisTemplate.opsForSet().add(KEY_PREFIX, itemInfo);
    }


}
