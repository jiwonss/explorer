package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
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
    private final Broadcasting broadcasting;

    /*
     * 파라미터 JSONObject json: ... channelId, userId, mapId, position
     */
    public Mono<Void> process(JSONObject json) {

        log.info("FARM process start...");

        return checkItemInMap(json)
                .flatMap(result -> {
                    if (result) {
                        // 파밍 성공
                        return farmItem(json)                           // redis-game map 상태 정보에서 파밍 오브젝트 삭제
                                .then(broadcastingSuccessData(json));   // BROADCASTING: 파밍 성공
                    } else {
                        // 파밍 실패
                        return unicastingFailData(json, "noItem");  // UNICASTING: 파밍 실패
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
     * [파밍 오브젝트 데이터 삭제 (redis-game)]
     *
     * redis-game의 map 상태 데이터 형식
     * - key:  mapData:{channelId}:{mapId}
     * - value (hash)
     *   - field: {position}
     *   - value: {itemCategory}:{isFarmable}:{itemId}:{itemCnt}
     */
    private Mono<Void> farmItem(JSONObject json) {
        log.info("farmItem");
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");

        return mapInfoRepository.deleteByPosition(channelId, mapId, position)
                .doOnSuccess(success -> log.info("SUCCESS delete farming object: {}", success))
                .doOnError(error -> log.error("ERROR delete farming object: {}", error.getMessage()));
    }

    /*
     * [파밍 성공 시 BROADCASTING]
     * 파라미터 JSONObject json: ... channelId, userId, mapId, position
     */
    private Mono<Void> broadcastingSuccessData(JSONObject json) {

        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");

        Map<Object, Object> dataBody = new HashMap<>();
        dataBody.put("mapId", mapId);
        dataBody.put("position", position);

        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success("farm", CastingType.BROADCASTING, dataBody))
                ).then();
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
