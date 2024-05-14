package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.InventoryLevelRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Upgrade {

    @Value("${logic.laboratory.upgrade-url}")
    private String upgradeUrl;

    private final InventoryLevelRepository inventoryLevelRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) {

        return checkLabLevel(json)                                                  // 1) 연구소 레벨 확인
                .flatMap(possible -> {
                    if(!possible) {                                                 // 2-1) 업그레이드 불가 레벨
                        log.info("fail");
                        return unicastingFailData(json, "cannotUpgrade");
                    } else {                                                        // 2-2) 업그레이드 가능 레벨
                        log.info("success");
                        return Mono.empty();
                    }
                });
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
    private Mono<Boolean> checkLabLevel(JSONObject json) {
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

    /*
     * [Unicasting : fail output data]
     * 파라미터
     * - JSONObject json : {..., "channelId":{channelId}, "userId":{userId}}
     * - String msg : "cannotUpgrade" 또는 "noItem"
     */
    private Mono<Void> unicastingFailData(JSONObject json, String msg) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("msg", msg);

        return unicasting.unicasting(channelId, userId,
                MessageConverter.convert(Message.fail("upgrade", CastingType.UNICASTING, dataBody)))
                .then();
    }

}
