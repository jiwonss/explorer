package com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class PlayerInfoRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "playerInfoData:";

    public PlayerInfoRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Void> save(String channelId, Long userId, String nickname, int avatar, int inventoryCnt) {
        String key = channelId + ":" + userId;
        return Mono.when(
                reactiveHashOperations.put(KEY_PREFIX + key, "nickname", nickname),
                reactiveHashOperations.put(KEY_PREFIX + key, "avatar", String.valueOf(avatar)),
                reactiveHashOperations.put(KEY_PREFIX + key, "inventoryCnt", String.valueOf(inventoryCnt))
        ).then();
    }

    public Mono<Object> findInventoryCnt(String channelId, Long userId) {
        String key = channelId + ":" + userId;
        return reactiveHashOperations.get(KEY_PREFIX + key, "inventoryCnt");
    }

}
