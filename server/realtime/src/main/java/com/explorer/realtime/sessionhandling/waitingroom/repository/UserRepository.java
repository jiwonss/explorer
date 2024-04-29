package com.explorer.realtime.sessionhandling.waitingroom.repository;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "user:";

    public UserRepository(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Void> save(UserInfo userInfo) {
        return Mono.when(
                reactiveHashOperations.put(KEY_PREFIX + userInfo.getUserId(), "nickname", userInfo.getNickname()),
                reactiveHashOperations.put(KEY_PREFIX + userInfo.getUserId(), "avatar", String.valueOf(userInfo.getAvatar()))
        ).then();
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

}
