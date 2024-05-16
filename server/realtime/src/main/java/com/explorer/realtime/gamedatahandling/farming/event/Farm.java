package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Farm {

    private final MapInfoRepository mapInfoRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) {

        log.info("FARM process start...");

        return checkItemInMap(json)
                .flatMap(result -> {
                    if (result) {
                        log.info("success");
                        return Mono.empty();
                    } else {
                        return unicastingFailData(json, "noItem");
                    }
                })
                .doOnError(error -> log.error("ERROR farm process: {}", error.getMessage()))
                .then();
    }

    /*
     * [redis-game의 map 상태 데이터에 {position}에 아이템이 있는지 확인]
     *
     * redis-game의 map 상태 데이터 형식
     * - key:  mapData:{channelId}:{mapId}
     * - value (hash)
     *   - field: {position}
     *   - value: {itemCategory}:{isFarmable}:{itemId}:{itemCnt}
     */
    private Mono<Boolean> checkItemInMap(JSONObject json) {
        log.info("checkItemInMap");
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");

        return mapInfoRepository.findByPosition(channelId, mapId, position)
                .map(result -> {
                    log.info("Item found: {}", result);
                    return true;
                })
                .defaultIfEmpty(false)
                .doOnError(error -> log.error("ERROR finding item in map: {}", error.getMessage()));
    }

    /*
     * [Unicasting : fail output data]
     * 파라미터
     * - JSONObject json : {..., "channelId":{channelId}, "userId":{userId}, "itemCategory" : "compound", "itemId" : {itemId} }
     * - String msg : "noItem"
     */
    private Mono<Void> unicastingFailData(JSONObject json, String msg) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("msg", msg);

        return unicasting.unicasting(channelId, userId,
                        MessageConverter.convert(Message.fail("farm", CastingType.UNICASTING, dataBody)))
                .then();
    }

}
