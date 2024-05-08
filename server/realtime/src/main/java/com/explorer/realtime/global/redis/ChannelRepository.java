package com.explorer.realtime.global.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class ChannelRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "channel:";

    public ChannelRepository(@Qualifier("channelReactiveRedisTemplate")ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(String channelId, Long userId, int mapId) {
        return reactiveHashOperations.put(KEY_PREFIX + channelId, String.valueOf(userId), String.valueOf(mapId));
    }

    public Mono<Map<Object, Object>> findAll(String channelId) {
        return reactiveHashOperations.entries(KEY_PREFIX+ channelId).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public Flux<Object> findAllFields(String channelId) {
        return reactiveRedisTemplate.opsForHash().keys(KEY_PREFIX+ channelId);
    }

    public Mono<Boolean> deleteAll(String channelId) {
        return reactiveHashOperations.delete(KEY_PREFIX + channelId);
    }

    public Mono<Long> deleteByUserId(String channelId, Long userId) {
        return reactiveHashOperations.remove(KEY_PREFIX + channelId, String.valueOf(userId));
    }

    public Mono<Long> count(String channelId) {
        return reactiveHashOperations.size(KEY_PREFIX + channelId);
    }

    public Mono<Boolean> exist(String channelId) {
        return reactiveRedisTemplate.hasKey(KEY_PREFIX + channelId);
    }

    public Mono<Boolean> existByUserId(String channelId, Long userId) {
        return reactiveHashOperations.hasKey(KEY_PREFIX + channelId, String.valueOf(userId));
    }

}
