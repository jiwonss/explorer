package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

@Repository("elementLaboratoryRepositoryInExtract")
public class ElementLaboratoryRepository {

    private final ReactiveListOperations<String, Object> listOperations;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "labData:";
    private static final String ELEMENT_SUFFIX = ":0:element";

    public ElementLaboratoryRepository(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.listOperations = reactiveRedisTemplate.opsForList();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<Void> updateValueAtIndex(String channelId, String result) {
        try {
            //
            String elementKey = KEY_PREFIX+channelId+ELEMENT_SUFFIX;

            // JSON 문자열을 Map으로 파싱
            Map<String, Integer> parsedData = objectMapper.readValue(result, Map.class);

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

}
