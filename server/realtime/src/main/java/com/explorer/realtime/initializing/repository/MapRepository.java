package com.explorer.realtime.initializing.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MapRepository {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ReactiveValueOperations<String, String> reactiveValueOperations;

    private static final String KEY_PREFIX = "map";

    public MapRepository(ReactiveRedisTemplate<String, String> reactiveRedisTemplate){
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveValueOperations = reactiveRedisTemplate.opsForValue();
    }

    public Mono<Boolean> saveMapData(Integer mapId, String positions) {
        return reactiveValueOperations.set(KEY_PREFIX + mapId, positions);
    }

    public Mono<String> findMapData(Integer mapId) {
        return reactiveValueOperations.get(KEY_PREFIX + mapId);
    }

}
