package com.explorer.realtime.gamedatahandling.farming.repository;

import com.explorer.realtime.gamedatahandling.farming.dto.PositionInfo;
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

    public MapInfoRepository(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, int mapId, PositionInfo positionInfo, String category, int itemId, int itemCnt) {
        String key = channelId + ":" + String.valueOf(mapId);
        String field = positionInfo.toString();
        String value = category + ":" + String.valueOf(itemId) + ":" + String.valueOf(itemCnt);
        return reactiveHashOperations.put(KEY_PREFIX + key + KEY_SUFFIX, field, value);
    }

    public Mono<Object> find(String channelId, int mapId, PositionInfo positionInfo) {
        String key = channelId + ":" + String.valueOf(mapId);
        String field = positionInfo.toString();
        return reactiveHashOperations.get(KEY_PREFIX + key + KEY_SUFFIX, field);
    }

}
