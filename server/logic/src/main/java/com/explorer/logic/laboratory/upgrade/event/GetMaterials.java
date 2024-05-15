package com.explorer.logic.laboratory.upgrade.event;

import com.explorer.logic.laboratory.upgrade.repository.UpgradeMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component("GetMaterialsForLabUpgrade")
@RequiredArgsConstructor
public class GetMaterials {

    private final UpgradeMaterialRepository upgradeMaterialRepository;

    /*
     * [연구소 업그레이드를 위한 재료 반환]
     * 파라미터 :
     * - String body : {"labId":{labId}, "labLevel":{labLevel}}
     */
    public Mono<String> process(String body) {
        log.info("Request body: {}", body);

        try {
            JSONObject json = new JSONObject(body);
            JSONObject responseJson = new JSONObject();

            int labId = json.getInt("labId");
            int labLevel = json.getInt("labLevel");

            // redis-staticgame에서 labLevel -> labLevel+1 로 upgrade하기 위한 재료 조회
            return upgradeMaterialRepository.findAll(labId, labLevel+1)
                    .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                            .doOnNext(entry -> {
                                responseJson.put(entry.getKey().toString(), entry.getValue());
                            })
                    )
                    .then(Mono.fromCallable(() -> {
                        log.info("responseJson: {}", responseJson);
                        return responseJson.toString(); // realtime 서버에 upgrade 재료 데이터 전송
                    }));

        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return Mono.just(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
