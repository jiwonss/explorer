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

    public FarmRepository(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {

        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Map<Object, Object>> findAll(String itemCategory, int itemId) {
        log.info("key : {}", itemCategory + itemId);
        return reactiveHashOperations.entries(itemCategory + ":" + itemId).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
