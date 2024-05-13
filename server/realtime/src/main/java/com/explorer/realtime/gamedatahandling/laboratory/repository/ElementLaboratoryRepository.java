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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository("elementLaboratoryRepositoryInExtract")
public class ElementLaboratoryRepository {

    private final ReactiveListOperations<String, Object> listOperations;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "labData:";
    private static final String ELEMENT_SUFFIX = ":0:element";
    private static final String COMPOUND_SUFFIX = ":0:compound";

    public ElementLaboratoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.listOperations = reactiveRedisTemplate.opsForList();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<List<Integer>> findAllElements(JSONObject json) {
        String channelId = json.getString("channelId");
        String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .cast(Integer.class)
                .collectList();
    }

    public Mono<List<Integer>> findAllCompounds(JSONObject json) {
        String channelId = json.getString("channelId");
        String elementKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;
        return listOperations.range(elementKey, 0, -1)
                .cast(Integer.class)
                .collectList();
    }

    public Mono<Boolean> findElement(String channelId, String info, int cnt) {
        String[] elementInfo = info.split(":");
        String elementKey = KEY_PREFIX+channelId+":0:"+elementInfo[0];
        int index = Integer.parseInt(elementInfo[1]);

        return listOperations.index(elementKey, index)
                .cast(Integer.class)
                .map(value -> value >= cnt)
                .defaultIfEmpty(false);
    }

    public Mono<Void> useElement(String channelId, String info, int cnt) {
        String[] elementInfo = info.split(":");
        String elementKey = KEY_PREFIX+channelId+":0:"+elementInfo[0];
        int index = Integer.parseInt(elementInfo[1]);

        return listOperations.index(elementKey, index)
                .cast(Integer.class)
                .doOnNext(currentValue -> log.info("[channelId:{}] Before using element - itemCategory:{}, index: {}, value:{}", channelId, elementInfo[0], index, currentValue))
                .flatMap(currentValue -> {
                    return listOperations.set(elementKey, index, currentValue - cnt)
                            .doOnSuccess(done -> log.info("[channelId:{}] After using element - itemCategory:{}, index: {}, value:{}", channelId, elementInfo[0], index, currentValue-cnt));
                })
                .then();
    }

    public Mono<Void> createCompound(String channelId, String itemCategory, int itemId) {
        String compoundKey = KEY_PREFIX+channelId+COMPOUND_SUFFIX;

        return listOperations.index(compoundKey, itemId)
                .cast(Integer.class)
                .doOnNext(currentValue -> log.info("[channelId:{}] Before create compound - itemCategory:{}, index: {}, value:{}", channelId, itemCategory, itemId, currentValue))
                .flatMap(currentValue -> {
                    return listOperations.set(compoundKey, itemId, currentValue + 1)
                            .doOnSuccess(done -> log.info("[channelId:{}] After using element - itemCategory:{}, index: {}, value:{}", channelId, itemCategory, itemId, currentValue + 1));
                })
                .then();
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
}
