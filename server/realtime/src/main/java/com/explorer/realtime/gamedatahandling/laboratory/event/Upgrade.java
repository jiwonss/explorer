package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.InventoryLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Upgrade {

    @Value("${logic.laboratory.upgrade-url}")
    private String upgradeUrl;

    private final InventoryLevelRepository inventoryLevelRepository;

    public Mono<Void> process(JSONObject json) {

        return checLabLevel(json)
                .doOnNext(possible -> log.info("upgrade possibility: {}", possible))
                .then();
    }

    /*
     * [연구소의 레벨을 확인한다]
     *
     * 파라미터
     * - 타입 : JSONObject
     * - 값 : {..., "channelId":{channelId}, "userId":{userId}}
     *
     * 반환값
     * - 타입 : Mono<Object>
     * - 값 : {level}
     *
     * 연구소 레벨 데이터 (redis-game)
     * key : labLevel:{channelId}:{labId}
     * value: {level}
     */
    private Mono<Boolean> checLabLevel(JSONObject json) {
        String channelId = json.getString("channelId");

        return inventoryLevelRepository.findLabLevel(channelId, 0)
                .map(levelStr -> {
                    try {
                        int level = Integer.parseInt(levelStr.toString());
                        return level>=0 && level<3;
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse lab level: {}", levelStr, e);
                        return false;
                    }
                });
    }

}
