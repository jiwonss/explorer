package com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class CurrentMapRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "currentMapId:";

    public CurrentMapRepository(@Qualifier("channelReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, Integer mapId) {
        return reactiveHashOperations.put(KEY_PREFIX + channelId, "mapId", String.valueOf(mapId));
    }

    public Mono<Object> findMapId(String channelId) {
        return reactiveHashOperations.get(KEY_PREFIX + channelId, "mapId");
    }

    public Mono<Boolean> delete(String channelId) {
        return reactiveHashOperations.delete(KEY_PREFIX + channelId);
    }
}
