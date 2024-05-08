package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Repository
public class InventoryRepositoryForLab {
    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "data:";
    private static final String KEY_SUFFIX = ":inventory";

    public InventoryRepositoryForLab(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
        this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.reactiveHashOperations = stringReactiveRedisTemplate.opsForHash();
    }

    public Mono<Map<Object, Object>> findAll(UserInfo userInfo) {
        log.info("InventoryRepositoryForLab findAll : {}", KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId() + KEY_SUFFIX);
        return reactiveHashOperations
                .entries(KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId() + KEY_SUFFIX)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .doOnError(error -> log.error("InventoryRepositoryForLab findAll error : {}", error.getMessage()));
    }

}
