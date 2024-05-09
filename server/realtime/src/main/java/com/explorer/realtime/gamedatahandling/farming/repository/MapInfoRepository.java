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

    private static final String KEY_PREFIX = "mapData:";

    public MapInfoRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    /*
     * key : mapData:{channelId}:{mapId}
     * field : {posX}:{posY}:{posZ}:{rotX}:{rotY}:{rotZ}
     * value : {itemCategory}:{itemId}
     * 반환값 :
     *      있는 경우 : {itemCategory}:{itemId}
     *      없는 경우 : empty Mono
     */
    public Mono<String> findByPosition(String channelId, int mapId, String position) {
        String key = KEY_PREFIX + channelId + ":" + String.valueOf(mapId);
        return reactiveHashOperations.get(key, position).map(Object::toString);
    }

}
