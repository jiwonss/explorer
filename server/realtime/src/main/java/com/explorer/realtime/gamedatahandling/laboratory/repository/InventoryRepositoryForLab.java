package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InventoryRepositoryForLab {
    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;

    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "inventoryData:";

    public InventoryRepositoryForLab(ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
        this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.reactiveHashOperations = stringReactiveRedisTemplate.opsForHash();
    }

    /*
     * [특정 플레이어의 인벤토리를 반환한다]
     */
    public Mono<Map<Object, Object>> findAll(UserInfo userInfo) {
        log.info("InventoryRepositoryForLab findAll : {}", KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId());
        return reactiveHashOperations
                .entries(KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId())
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .defaultIfEmpty(Collections.emptyMap())
                .doOnError(error -> log.error("InventoryRepositoryForLab findAll error : {}", error.getMessage()));
    }

    /*
     * [추출된 아이템을 inventory에서 삭제]
     * hasItemInventoryIds 리스트에 포함되어 있는 field 값을 삭제한다
     * 모든 필드가 삭제되면 해당 키 자체도 자동으로 삭제된다
     * => 인벤토리에 아이템이 없으면 인벤토리 데이터가 redis-ingame에서 사라진다
     */
    public Mono<Void> deleteFields(UserInfo userInfo, List<Object> hasItemInventoryIds) {
        String redisKey = KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId();

        return reactiveHashOperations
                .entries(redisKey)
                .filter(entry -> hasItemInventoryIds.contains(entry.getKey().toString()))
                .flatMap(entry -> reactiveHashOperations.remove(redisKey, entry.getKey()))
                .then()
                .doOnError(error -> log.error("Error deleting fields: {}", error.getMessage()))
                .doOnSuccess(success -> log.info("Successfully deleted specified fields from {}", redisKey));
    }
}
