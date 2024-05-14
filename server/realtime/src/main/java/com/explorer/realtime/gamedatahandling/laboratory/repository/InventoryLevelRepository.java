package com.explorer.realtime.gamedatahandling.laboratory.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class InventoryLevelRepository {

    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;
    private final ReactiveValueOperations<String, Object> reactiveValueOperations;
    private static final String KEY_PREFIX = "labLevel:";

    public InventoryLevelRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
        this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.reactiveValueOperations = stringReactiveRedisTemplate.opsForValue();
    }

    /*
     * [연구소의 현재 레벨을 조회한다]
     * 반환값
     * - 값 : {level}
     *
     * 연구소 레벨 데이터
     * key : labLevel:{channelId}:{labId}
     * value: {level}
     */
    public Mono<Object> findLabLevel(String channelId, int labId) {
        log.info("find {} Lab LEVEL", labId);
        String redisKey = KEY_PREFIX + channelId + ":" + labId;
        return reactiveValueOperations.get(redisKey)
                .doOnSuccess(value -> {
                    if (value != null) {
                        log.info("Found LabLevel for channel {} : {}", channelId, value);
                    } else {
                        log.warn("No LabLevel for channel {}", channelId);
                    }
                })
                .doOnError(error -> log.error("Failed to find LabLevel for channel {}", channelId, error));

    }

}
