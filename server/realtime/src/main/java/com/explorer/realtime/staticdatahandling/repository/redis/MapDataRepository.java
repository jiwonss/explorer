package com.explorer.realtime.staticdatahandling.repository.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MapDataRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "mapData:";

    public MapDataRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, int mapId, String position, String itemCategory, int itemId) {
        String key = KEY_PREFIX + channelId + ":" + String.valueOf(mapId);
        String value = itemCategory + ":isFarmable:" + String.valueOf(itemId);
        return reactiveHashOperations.put(key, position, value);
    }

}
