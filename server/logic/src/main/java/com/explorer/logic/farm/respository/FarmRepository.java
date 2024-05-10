package com.explorer.logic.farm.respository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Repository
public class FarmRepository {
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "farm:";

    public FarmRepository(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {

        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    /*
     * field : {itemCategory}:{itemId}
     * value : {maxCnt}
     */
    public Mono<Map<Object, Object>> findAll(String itemCategory, int itemId) {
        log.info("key : {}", KEY_PREFIX + itemCategory + ":" + itemId);
        return reactiveHashOperations.entries(KEY_PREFIX + itemCategory + ":" + itemId).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
