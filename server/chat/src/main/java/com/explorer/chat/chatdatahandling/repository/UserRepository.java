package com.explorer.chat.chatdatahandling.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class UserRepository {
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "user:";

    public UserRepository(@Qualifier("customReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }
    public Mono<Map<Object, Object>> findAll(Long userId) {
        return reactiveHashOperations.entries(KEY_PREFIX + userId).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
