package com.explorer.realtime.staticdatahandling.repository.redis;

import com.explorer.realtime.staticdatahandling.dto.MaterialItemInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SynthesizedMaterialRepository {

    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "synthesize:compound:";

    public SynthesizedMaterialRepository(@Qualifier("staticgameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> save(int id, MaterialItemInfo materialItemInfo) {
        String key = KEY_PREFIX + String.valueOf(id);
        String field = materialItemInfo.getCategory() + ":" + String.valueOf(materialItemInfo.getId());
        String value = String.valueOf(materialItemInfo.getCnt());
        return reactiveHashOperations.put(key, field, value);
    }


}
