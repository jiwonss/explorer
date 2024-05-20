package com.explorer.logic.laboratory.extract.event;

import com.explorer.logic.laboratory.extract.repository.ExtractionMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Random;

@Slf4j
@Component("labCalculate")
@RequiredArgsConstructor
public class Calculate {

    private final ExtractionMaterialRepository extractionMaterialRepository;

    public Mono<String> process(String body) {
        log.info("Request body: {}", body);

        try {
            JSONObject json = new JSONObject(body);
            JSONObject responseJson = new JSONObject();
            int itemId = json.getInt("itemId");
            Random random = new Random();

            // 추출 가능한 아이템 ID 목록 가져오기
            return findAllextractableItemIds(itemId)
                    .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                            .doOnNext(entry -> {

                                int maxCnt = Integer.parseInt(entry.getValue().toString());

                                /*
                                 * [지수 분포]
                                 * lambda
                                 *  - 값이 작을수록 0에 가까운 값들이 자주 발생
                                 *  - 값이 클수록 maxCnt에 가까운 값들이 자주 발생
                                 *
                                 */
                                double lambda = 3.0 / maxCnt;  // 'lambda' 값을 조정하여 분포의 형태를 조절
                                double exponentialValue = Math.log(1 - random.nextDouble()) / -lambda;
                                int exponentialRandomCount = (int) Math.min(maxCnt, exponentialValue);

                                /*
                                 * [선형분포]
                                 */
//                                int randomCount = random.nextInt(maxCnt + 1);

                                responseJson.put(entry.getKey().toString(), exponentialRandomCount);
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

    private Mono<Map<Object, Object>> findAllextractableItemIds(int itemId) {
        return extractionMaterialRepository.findAll(itemId);
    }
}
