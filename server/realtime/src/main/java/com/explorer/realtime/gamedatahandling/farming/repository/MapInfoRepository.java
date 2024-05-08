package com.explorer.realtime.gamedatahandling.farming.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MapInfoRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "data:";
    private static final String KEY_SUFFIX = ":mapInfo";

    public MapInfoRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, int mapId, String position, String category, int itemId, int itemCnt) {
        String key = channelId + ":" + mapId;
        String value = category + ":" + itemId + ":" + itemCnt;
        return reactiveHashOperations.put(KEY_PREFIX + key + KEY_SUFFIX, position, value);
    }

    public Mono<Object> find(String channelId, int mapId, String position) {
        String key = channelId + ":" + mapId;
        return reactiveHashOperations.get(KEY_PREFIX + key + KEY_SUFFIX, position);
    }

}
