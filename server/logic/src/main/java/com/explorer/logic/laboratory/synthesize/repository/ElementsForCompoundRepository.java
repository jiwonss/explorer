package com.explorer.logic.laboratory.synthesize.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Repository
public class ElementsForCompoundRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;
    private static final String KEY_PREFIX = "synthesize:compound:";

    public ElementsForCompoundRepository(@Qualifier("stringReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {

        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Map<Object, Object>> findAll(int itemId) {
        log.info("key : {}", KEY_PREFIX + itemId);
        return reactiveHashOperations.entries(KEY_PREFIX + itemId).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}