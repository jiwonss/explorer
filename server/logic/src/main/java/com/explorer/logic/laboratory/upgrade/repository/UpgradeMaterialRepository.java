package com.explorer.logic.laboratory.upgrade.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Repository
public class UpgradeMaterialRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;
    private static final String KEY_PREFIX = "upgradeMaterial:";
    private static final String[] LAB_ID = {"elementLab:"};  // labId에 따른 KEY 요소

    public UpgradeMaterialRepository(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {

        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    /*
     * [연구소 업그레이드 재료 in redis-staticgame]
     * key:  upgradeMaterial:elementLab:{level}
     * field:  {itemCategory}:{itemId}
     * value:  {itemCnt}
     */
    public Mono<Map<Object, Object>> findAll(int labId, int labLevel) {
        String redisKey = KEY_PREFIX + LAB_ID[labId] + labLevel;
        return reactiveHashOperations.entries(redisKey).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
