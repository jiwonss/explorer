package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository("elementLaboratoryRepositoryInExtract")
public class ElementLaboratoryRepository {

    private final ReactiveListOperations<String, Object> reactiveListOperations;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "labData:";
    private static final String ELEMENT_SUFFIX = ":0:element";
    private static final String COMPOUND_SUFFIX = ":0:compound";

    public ElementLaboratoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveListOperations = reactiveRedisTemplate.opsForList();
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<List<Integer>> findAllElements(JSONObject json) {
        String channelId = json.getString("channelId");
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return reactiveListOperations.range(elementKey, 0, -1)
                .cast(Integer.class)
                .collectList();
    }

    public Mono<List<Integer>> findAllCompounds(JSONObject json) {
        String channelId = json.getString("channelId");
        String elementKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return reactiveListOperations.range(elementKey, 0, -1)
                .cast(Integer.class)
                .collectList();
    }

    /*
     * redis-game의 연구소 저장 상태 데이터
     * key: labData:{channelId}+:{labId}:{itemCategory}
     * value (list)
     *  - index: {itemId}
     *  - value: {itemCnt}
     */
    public Mono<Boolean> findMaterial(String channelId, String info, int cnt) {
        String[] elementInfo = info.split(":");  // [0]:itemCategory  [1]:itemId
        String redisKey = KEY_PREFIX+channelId+":0:"+elementInfo[0];
        int itemId = Integer.parseInt(elementInfo[1]);

        return reactiveListOperations.index(redisKey, itemId)
                .cast(Integer.class)
                .map(value -> value >= cnt)
                .defaultIfEmpty(false);
    }

    public Mono<Void> useMaterial(String channelId, String info, int cnt) {
        String[] elementInfo = info.split(":");
        String elementKey = KEY_PREFIX+channelId+":0:"+elementInfo[0];
        int index = Integer.parseInt(elementInfo[1]);

        return reactiveListOperations.index(elementKey, index)
                .cast(Integer.class)
                .doOnNext(currentValue -> log.info("[channelId:{}] Before using element - itemCategory:{}, index: {}, value:{}", channelId, elementInfo[0], index, currentValue))
                .flatMap(currentValue -> {
                    return reactiveListOperations.set(elementKey, index, currentValue - cnt)
                            .doOnSuccess(done -> log.info("[channelId:{}] After using element - itemCategory:{}, index: {}, value:{}", channelId, elementInfo[0], index, currentValue-cnt));
                })
                .then();
    }

    public Mono<Void> createCompound(String channelId, String itemCategory, int itemId) {
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;

        return reactiveListOperations.index(compoundKey, itemId)
                .cast(Integer.class)
                .doOnNext(currentValue -> log.info("[channelId:{}] Before create compound - itemCategory:{}, index: {}, value:{}", channelId, itemCategory, itemId, currentValue))
                .flatMap(currentValue -> {
                    return reactiveListOperations.set(compoundKey, itemId, currentValue + 1)
                            .doOnSuccess(done -> log.info("[channelId:{}] After using element - itemCategory:{}, index: {}, value:{}", channelId, itemCategory, itemId, currentValue + 1));
                })
                .then();
    }

    /*
     * [원소 추출 후 redis-game 연구소에 저장]
     *
     * redis-game 연구소 상태 데이터 형식
     * - key: labData:{channelId}:0:element
     * - value: List
     *   - index: {itemId}
     *   - value : {itemCnt}
     */
    public Mono<Void> UpdateItemCnt(String channelId, String itemCategory, int itemId, int itemCnt) {

        String suffix = itemCategory.equals("element") ? ELEMENT_SUFFIX : itemCategory.equals("compound") ? COMPOUND_SUFFIX : "";
        String redisKey = KEY_PREFIX + channelId + suffix;


        return reactiveListOperations.index(redisKey, itemId)
                .defaultIfEmpty(0)
                .map(existingCnt -> { // 기존의 원소 개수
                    int currentCount = (existingCnt instanceof Integer) ? (Integer) existingCnt : Integer.parseInt(existingCnt.toString());
                    return currentCount + itemCnt; // 원소 게수 업데이트
                })
                .flatMap(updatedCnt -> reactiveListOperations.set(redisKey, itemId, updatedCnt))
                .doOnError(error -> log.error("ERROR UpdateItemCnt: {}", error.getMessage()))
                .then();
    }

    public Flux<Integer> findAllElementData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return reactiveListOperations.range(elementKey, 0, -1)
                .map(this::convertToInt);
    }

    public Flux<Integer> findAllCompoundData(String channelId) {
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return reactiveListOperations.range(compoundKey, 0, -1)
                .map(this::convertToInt);
    }
    public Mono<Boolean> deleteAllData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        reactiveListOperations.delete(compoundKey).subscribe();
        return reactiveListOperations.delete(elementKey);
    }

    private Integer convertToInt(Object data) {
        return Integer.parseInt(data.toString());
    }

    public Mono<Void> save(String key, List<Integer> itemCntList) {
        return Flux.fromIterable(itemCntList)
                .flatMap(value -> reactiveListOperations.rightPush(key, value))
                .then();
    }

    public Mono<Boolean> exist(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return reactiveRedisTemplate.hasKey(elementKey);
    }

    public Mono<List<Integer>> findElementData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return reactiveListOperations.range(elementKey, 0, -1)
                .map(this::convertToInt)
                .collectList();
    }

    public Mono<List<Integer>> findCompoundData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return reactiveListOperations.range(elementKey, 0, -1)
                .map(this::convertToInt)
                .collectList();
    }
}
