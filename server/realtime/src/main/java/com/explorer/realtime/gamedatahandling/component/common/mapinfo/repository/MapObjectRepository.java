package com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MapObjectRepository {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ReactiveValueOperations<String, String> reactiveValueOperations;

    private static final String KEY_PREFIX = "mapData";

    public MapObjectRepository(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveValueOperations = reactiveRedisTemplate.opsForValue();
    }

    public Mono<Boolean> saveMapData(String channelId, Integer mapId, String data) {
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return reactiveValueOperations.set(key, data);
    }
}
