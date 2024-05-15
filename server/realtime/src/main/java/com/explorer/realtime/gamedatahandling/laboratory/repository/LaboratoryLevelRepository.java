package com.explorer.realtime.gamedatahandling.laboratory.repository;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class LaboratoryLevelRepository {

    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;
    private final ReactiveValueOperations<String, Object> reactiveValueOperations;
    private static final String KEY_PREFIX = "labLevel:";

    public LaboratoryLevelRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
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
        String redisKey = KEY_PREFIX + channelId + ":" + labId;
        return reactiveValueOperations.get(redisKey)
                .doOnError(error -> log.error("Failed to find LabLevel for channel {}", channelId, error));

    }

    /*
     * [연구소 레벨업]
     * 연구소 레벨 데이터
     * key : labLevel:{channelId}:{labId}
     * value: {level}
     */
    public Mono<Void> incLabLevel(JSONObject json) {
        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");
        String redisKey = KEY_PREFIX + channelId + ":" + labId;

        // Get the current level from Redis, increment it, and save it back
        return reactiveValueOperations
                .get(redisKey)
                .defaultIfEmpty("0") // If there's no current level, start from "0"
                .map(value -> Integer.parseInt(value.toString()))
                .flatMap(level -> reactiveValueOperations.set(redisKey, level+1)) // Save the new level back to Redis
                .then(); // Return an empty Mono<Void> to signal completion
    }

}
