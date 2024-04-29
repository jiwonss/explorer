package com.explorer.realtime.sessionhandling.waitingroom.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChannelRepository {


    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "channel:";

    public Mono<Long> save(String teamCode, Long userId) {
        return reactiveRedisTemplate.opsForList().rightPush(KEY_PREFIX + teamCode, String.valueOf(userId));
    }

    public Mono<Long> count(String teamCode) {
        return reactiveRedisTemplate.opsForList().size(KEY_PREFIX + teamCode);
    }

}
