package com.explorer.realtime.sessionhandling.ingame.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Repository("LaboratoryLevelRepositoryForInitializing")
public class LaboratoryLevelRepository {

    private final ReactiveValueOperations<String, Object> valueOperations;

    private static final String KEY_PREFIX = "labLevel:";
    private static final String ELEMENT_SUFFIX = ":0";

    public LaboratoryLevelRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.valueOperations = reactiveRedisTemplate.opsForValue();
    }

    public Mono<Void> initialize(String channelId) {
        String elementLaboratoryKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;

        return valueOperations.set(elementLaboratoryKey, 0).then();
    }

    public Mono<Boolean> delete(String channelId) {
        String elementLaboratoryKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return valueOperations.delete(elementLaboratoryKey);
    }

    public Mono<Object> findValue(String channelId) {
        String elementLaboratoryKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return valueOperations.get(elementLaboratoryKey);
    }

    public Mono<Boolean> save(String channelId, String value) {
        String elementLaboratoryKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return valueOperations.set(elementLaboratoryKey, value);
    }
}
