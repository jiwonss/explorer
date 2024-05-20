package com.explorer.realtime.sessionhandling.ingame.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Repository("elementLaboratoryRepositoryInStartGame")
public class ElementLaboratoryRepository {

    private final ReactiveListOperations<String, Object> listOperations;

    private static final String KEY_PREFIX = "labData:";
    private static final String ELEMENT_SUFFIX = ":0:element";
    private static final String COMPOUND_SUFFIX = ":0:compound";

    public ElementLaboratoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.listOperations = reactiveRedisTemplate.opsForList();
    }

    public Mono<Void> initialize(String channelId) {
        Object[] initialElements = new Object[20];
        Arrays.fill(initialElements, 0);

        Object[] initialCompounds = new Object[20];
        Arrays.fill(initialCompounds, 0);
        initialCompounds[0] = 1;
        initialCompounds[4] = 1;

        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        String compoundKey = KEY_PREFIX + channelId + COMPOUND_SUFFIX;

        return listOperations.rightPushAll(elementKey, Arrays.asList(initialElements))
                .then(listOperations.rightPushAll(compoundKey, Arrays.asList(initialCompounds)))
                .then();
    }
}
