package com.explorer.logic.laboratory.synthesize.event;

import com.explorer.logic.laboratory.synthesize.repository.ElementsForCompoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component("labGetElements")
@RequiredArgsConstructor
public class GetElements {

    private final ElementsForCompoundRepository elementsForCompoundRepository;

    public Mono<String> process(String body) {
        log.info("Request body: {}", body);

        try {
            JSONObject json = new JSONObject(body);
            JSONObject responseJson = new JSONObject();
            int itemId = json.getInt("itemId");

            return findAllElementsForCompound(itemId)
                    .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                            .doOnNext(entry -> {
                                responseJson.put(entry.getKey().toString(), entry.getValue());
                            })
                    )
                    .then(Mono.fromCallable(() -> {
                        log.info("responseJson: {}", responseJson);
                        return responseJson.toString();
                    }));

        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            // 오류 메시지 반환
            return Mono.just(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    private Mono<Map<Object, Object>> findAllElementsForCompound(int itemId) {
        return elementsForCompoundRepository.findAll(itemId);
    }
}