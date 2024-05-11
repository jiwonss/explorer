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

    public Mono<Void> init(String channelId, Long userId, String nickname, int avatar, int inventoryCnt) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return Mono.when(
                reactiveHashOperations.put(key, "nickname", nickname),
                reactiveHashOperations.put( key, "avatar", String.valueOf(avatar)),
                reactiveHashOperations.put(key, "inventoryCnt", String.valueOf(inventoryCnt)),
                reactiveHashOperations.put(key, "tool", String.valueOf(-1))
        ).then();
    }

    public Mono<Object> findInventoryCnt(String channelId, Long userId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.get(key, "inventoryCnt");
    }

    public Mono<Boolean> saveTool(String channelId, Long userId, int toolIdx) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.put(key, "tool", String.valueOf(toolIdx));
    }

    public Mono<Object> findTool(String channelId, Long userId) {
        String key = KEY_PREFIX + channelId + ":" + userId;
        return reactiveHashOperations.get(key, "tool");
    }

}
