package com.explorer.realtime.initializing.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class MapRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "map";

    public MapRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate){
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Long> saveMapData(Integer mapId, List<String> positions) {
        String key = KEY_PREFIX + mapId;
        return Flux.fromIterable(positions)
                .flatMap(position -> reactiveRedisTemplate.opsForList().rightPush(key, position))
                .count();
//        return reactiveRedisTemplate.opsForList().rightPush(KEY_PREFIX + mapId, positions);
    }

    public Flux<String> findMapData(Integer mapId) {
        return reactiveRedisTemplate.opsForList().range(KEY_PREFIX + mapId, 0, -1)
                .map(Object::toString);
    }

}
