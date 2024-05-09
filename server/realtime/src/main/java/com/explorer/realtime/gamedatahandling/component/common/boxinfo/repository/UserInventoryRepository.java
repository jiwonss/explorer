package com.explorer.realtime.gamedatahandling.component.common.boxinfo.repository;

import com.explorer.realtime.gamedatahandling.component.common.boxinfo.dto.InventoryItemInfo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository

public class UserInventoryRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private static final String KEY_PREFIX = "inventoryInfoData:";

    public UserInventoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<InventoryItemInfo> findInventoryItem(String channelId, Long userId, Integer inventoryIdx) {
        String key = "inventoryData:" + channelId + ":" + String.valueOf(userId);
        return reactiveHashOperations.get(key, String.valueOf(inventoryIdx))
                .map(InventoryItemInfo::of);
    }

    public Mono<Long> deleteInventoryItem(String channelId, Long userId, Integer inventoryIdx) {
        String key = "inventoryData:" + channelId + ":" + String.valueOf(userId);
        String inventoryIndex = String.valueOf(inventoryIdx);
        return reactiveHashOperations.remove(key, inventoryIndex);
    }
}
