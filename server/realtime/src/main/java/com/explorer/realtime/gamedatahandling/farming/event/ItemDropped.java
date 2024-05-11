package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.dto.FarmingItemInfo;
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
public class ItemDropped {

    private final MapInfoRepository mapInfoRepository;
    private final Broadcasting broadcasting;

    public Mono<Void> process(JSONObject json) {

        /*
         * 1) Parsing : channelId, mapId, position, oldPosition, userId, itemCategory, itemId
         */
        FarmingItemInfo droppedItemInfo = FarmingItemInfo.droppedOf(json);

        /*
         * 2) redis-game의 map 상태 업데이트
         */
        updateDroppedItemData(droppedItemInfo).subscribe();

        /*
         * 3) broadcasting : 아이템의 새로운 위치를 브로드캐스팅한다
         */
        return broadcastingDroppedItemData(droppedItemInfo);
    }

    private Mono<Boolean> updateDroppedItemData(FarmingItemInfo droppedItemInfo) {
        return mapInfoRepository.deleteOldPosition(droppedItemInfo)
                .then(mapInfoRepository.save(droppedItemInfo));
    }

    private Mono<Void> broadcastingDroppedItemData(FarmingItemInfo droppedItemInfo) {

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("mapId", droppedItemInfo.getMapId());
        dataBody.put("itemCategory", droppedItemInfo.getItemCategory());
        dataBody.put("itemId", droppedItemInfo.getItemId());
        dataBody.put("position", droppedItemInfo.getPosition());

        // 모든 키-값 쌍을 로깅
        for (Map.Entry<String, Object> entry : dataBody.entrySet()) {
            log.info("{}: {}", entry.getKey(), entry.getValue());
        }

        String dataBodyString = new JSONObject(dataBody).toString();

        return broadcasting.broadcasting(
                droppedItemInfo.getChannelId(),
                MessageConverter.convert(Message.success("itemDropped", CastingType.BROADCASTING, String.valueOf(dataBody)))
        );
    }

}
