package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository("elementLaboratoryRepositoryInExtract")
public class ElementLaboratoryRepository {

    private final ReactiveListOperations<String, Object> listOperations;
    private final ObjectMapper objectMapper;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "labData:";
    private static final String ELEMENT_SUFFIX = ":0:element";
    private static final String COMPOUND_SUFFIX = ":0:compound";

    public ElementLaboratoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.listOperations = reactiveRedisTemplate.opsForList();
        this.objectMapper = new ObjectMapper();
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<List<Integer>> findAllElements(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .cast(Integer.class)
                .collectList();
    }

    public Mono<Void> updateValueAtIndex(String channelId, String response) {
        try {
            //
            String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;

            // JSON 문자열을 Map으로 파싱
            Map<String, Integer> parsedData = objectMapper.readValue(response, Map.class);

            // 각 key-value 쌍에 대해 업데이트 수행
            Iterator<Map.Entry<String, Integer>> iterator = parsedData.entrySet().iterator();
            Mono<Void> updateChain = Mono.empty();

            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                int itemId = Integer.parseInt(entry.getKey().split(":")[1]);
                Integer itemCnt = entry.getValue();

                updateChain = updateChain.then(
                        listOperations.index(elementKey, itemId)
                                .defaultIfEmpty(0)
                                .cast(Integer.class)
                                .flatMap(existingCnt -> listOperations.set(elementKey, itemId, Integer.valueOf(existingCnt) + itemCnt))
                                .then()
                );

            }
            return updateChain.then();

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error processing JSON data", e));
        }
    }

    public Flux<Integer> findAllElementData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .map(this::convertToInt);
    }

    public Flux<Integer> findAllCompoundData(String channelId) {
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return listOperations.range(compoundKey, 0, -1)
                .map(this::convertToInt);
    }
    public Mono<Boolean> deleteAllData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        listOperations.delete(compoundKey).subscribe();
        return listOperations.delete(elementKey);
    }

    private Integer convertToInt(Object data) {
        return Integer.parseInt(data.toString());
    }

    public Mono<Void> save(String key, List<Integer> itemCntList) {
        return Flux.fromIterable(itemCntList)
                .flatMap(value -> listOperations.rightPush(key, value))
                .then();
    }

    public Mono<Boolean> exist(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return reactiveRedisTemplate.hasKey(elementKey);
    }

    public Mono<List<Integer>> findElementData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .map(this::convertToInt)
                .collectList();
    }

    public Mono<List<Integer>> findCompoundData(String channelId) {
        String elementKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .map(this::convertToInt)
                .collectList();
    }
}
