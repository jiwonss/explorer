package com.explorer.realtime.gamedatahandling.farming.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FarmableRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveSetOperations<String, Object> reactiveSetOperations;

    private static final String KEY = "farmable";

    public FarmableRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveSetOperations = reactiveRedisTemplate.opsForSet();
    }

    public Mono<Boolean> isFarmable(String itemCategory) {
        return reactiveSetOperations.isMember(KEY, itemCategory);
    }
}
