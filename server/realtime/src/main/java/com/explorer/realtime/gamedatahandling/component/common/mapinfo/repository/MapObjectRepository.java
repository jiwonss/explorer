package com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class MapObjectRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, String, String> hashOperations;

    private static final String KEY_PREFIX = "mapData";

    public MapObjectRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.hashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> saveMapData(String channelId, Integer mapId, List<String> positions, String itemCategory, Integer itemId) {
        Map<String, String> hashData = dataToHash(positions, itemCategory, itemId);
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return hashOperations.putAll(key, hashData)
                .map(result -> result == Boolean.TRUE);
    }

    private Map<String, String> dataToHash(List<String> positions, String itemCategory, Integer itemId) {
        Map<String, String> hashData = new HashMap<>();
        for (String position : positions) {
            hashData.put(position, itemCategory + ":" + "isFarmable" + ":" + itemId);
        }
        return hashData;
    }

    public Mono<Map<String, String>> findMapData(String channelId, Integer mapId) {
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return hashOperations.entries(key)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
    public Mono<Boolean> save(String channelId, int mapId, String position, String itemCategory, String isFarmable, int itemId) {
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        String value = itemCategory + ":" + isFarmable + ":" + itemId;
        return hashOperations.put(key, position, value);
    }

    public Mono<Boolean> resetMapData(String channelId, Integer mapId) {
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return reactiveRedisTemplate.delete(key)
                .map(count -> count > 0);
    }

    public Mono<Boolean> deleteAllMap(String channelId) {
        List<Integer> mapIds = Arrays.asList(1, 2, 3);
        return Flux.fromIterable(mapIds)
                .flatMap(mapid -> resetMapData(channelId, mapid))
                .reduce(true, (allSuccess, success) -> allSuccess && success);
    }
}

