package com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class InventoryRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, String, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "inventoryData:";

    public InventoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Void> init(String channelId, Long userId, int inventoryCnt) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return Flux.range(0, inventoryCnt)
                .flatMap(idx -> {
                    return reactiveHashOperations.put(key, String.valueOf(idx), "");
                })
                .then();
    }

    public Mono<Boolean> save(String channelId, Long userId, InventoryInfo inventoryInfo) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        String field = String.valueOf(inventoryInfo.getInventoryIdx());
        String value = inventoryInfo.toString();
        return reactiveHashOperations.put(key, field, value);
    }

    public Mono<Object> findByInventoryIdx(String channelId, Long userId, int inventoryIdx) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.get(key, String.valueOf(inventoryIdx))
                .switchIfEmpty(Mono.just(""));
    }

    public Mono<Long> deleteByInventoryIdx(String channelId, Long userId, int inventoryIdx) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.remove(key, String.valueOf(inventoryIdx));
    }

    public Mono<Boolean> deleteUserInventory(String channelId, Long userId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.delete(key);
    }

    public Mono<Map<String, String>> findInventoryData(String channeId, Long userId) {
        String key = KEY_PREFIX + channeId + ":" + userId;
        return reactiveHashOperations.entries(key)
                .collectMap(Map.Entry::getKey,entry -> (String)entry.getValue());
    }
}
