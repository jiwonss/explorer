package com.explorer.realtime.sessionhandling.waitingroom.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TempRepository {


    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "channel:";

    public Mono<Long> save(String teamCode, Long userId) {
        return reactiveRedisTemplate.opsForSet().add(KEY_PREFIX + teamCode, String.valueOf(userId));
    }

    public Mono<Long> count(String teamCode) {
        return reactiveRedisTemplate.opsForSet().size(KEY_PREFIX + teamCode);
    }

    public Flux<String> find(String teamCode) {
        return reactiveRedisTemplate.opsForSet().members(KEY_PREFIX + teamCode);
    }

    public Mono<Boolean> delete(String teamCode) {
        return reactiveRedisTemplate.opsForSet().delete(KEY_PREFIX + teamCode);
    }

    public Mono<Long> leave(String teamCode, Long userId) {
        return reactiveRedisTemplate.opsForSet().remove(KEY_PREFIX + teamCode, String.valueOf(userId));
    }

}
