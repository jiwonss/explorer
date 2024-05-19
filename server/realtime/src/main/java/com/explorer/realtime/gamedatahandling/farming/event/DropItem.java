package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
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
public class DropItem {

    private final MapInfoRepository mapInfoRepository;
    private final Broadcasting broadcasting;

    /*
     * 파라미터 JSONObject json: ... channelId, mapId, position, itemCategory, itemId, itemCnt
     */
    public Mono<Void> process(JSONObject json) {
        Long userId = json.getLong("userId");
        log.info("DropItem start...");
        return updateDroppedItemData(json)
                .then(broadcastingSuccessData(userId, json))
                .doOnError(error -> log.error("error: {}", error.getMessage()));
    }

    /*
     * [redis-game에 드랍 아이템 위치 저장]
     *
     * redis-game의 map 상태 정보
     * - key:  mapData:{channelId}:{mapId}
     * - value (hash)
     *   - field:  {position}
     *   - value:  {itemCategory}:{isFarmable}:{itemId}:{itemCnt}
     */
    private Mono<Void> updateDroppedItemData(JSONObject json) {
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");
        String itemCategory = json.getString("itemCategory");
        int itemId = json.getInt("itemId");
        int itemCnt = json.getInt("itemCnt");
        return mapInfoRepository.save(channelId, mapId, position, itemCategory, itemId, itemCnt).then();
    }

    /*
     * [파밍 성공 시 BROADCASTING]
     * 파라미터 JSONObject json: ... channelId, mapId, position, itemCategory, itemId, itemCnt
     */
    private Mono<Void> broadcastingSuccessData(Long userId, JSONObject json) {

        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");
        String itemCategory = json.getString("itemCategory");
        int itemId = json.getInt("itemId");
        int itemCnt = json.getInt("itemCnt");

        Map<Object, Object> dataBody = new HashMap<>();
        dataBody.put("mapId", mapId);
        dataBody.put("position", position);
        dataBody.put("itemCategory", itemCategory);
        dataBody.put("itemId", itemId);
        dataBody.put("itemCnt", itemCnt);
        dataBody.put("userId", userId);

        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success("dropItem", CastingType.BROADCASTING, dataBody)));
    }

}
