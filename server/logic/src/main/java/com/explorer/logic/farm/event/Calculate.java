package com.explorer.logic.farm.event;

import com.explorer.logic.farm.respository.FarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Random;

@Slf4j
@Component("farmCalculate")
@RequiredArgsConstructor
public class Calculate {

    private final FarmRepository farmRepository;

    public Mono<String> process(String body) {  // body : {itemCategory}:{isFarmable}:{itemId}
        log.info("Request body: {}", body);

        try {
            String[] parsedItemInfo = body.split(":"); // 0:itemCategory, 1:isFarming, 2:itemId
            String itemCategory = parsedItemInfo[0];
            int itemId = Integer.valueOf(parsedItemInfo[2]);

            JSONObject responseJson = new JSONObject();
            Random random = new Random();

            return findAllDroppedItemIds(itemCategory, itemId)
                    .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                            .doOnNext(entry -> {

                                int maxCnt = Integer.parseInt(entry.getValue().toString());
                                int randomCount = random.nextInt(maxCnt + 1);  // 랜덤 카운트 생성

                                responseJson.put(entry.getKey().toString(), randomCount);
                            })
                    )
                    .then(Mono.fromCallable(() -> {
                        log.info("responseJson: {}", responseJson);
                        return responseJson.toString();  // 결과 JSON 반환
                    }));

        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            // 오류 메시지 반환
            return Mono.just(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    private Mono<Map<Object, Object>> findAllDroppedItemIds(String itemCategory, int itemId) {
        return farmRepository.findAll(itemCategory, itemId);
    }
}
