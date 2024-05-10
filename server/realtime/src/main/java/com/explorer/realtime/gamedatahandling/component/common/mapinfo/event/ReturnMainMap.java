package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnMainMap {

    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;

    public Mono<Void> returnMainMap(JSONObject json) {
        String channelId = json.getString("channelId");
        Integer mapId = json.getInt("mapId");
        if (mapId == 4) {
            mapObjectRepository.resetMapData(channelId, mapId).subscribe();
        }
        // else mongodb에 데이터 저장하기
        return mapObjectRepository.findMapData(channelId, 1)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("returnMainMap", CastingType.BROADCASTING, mapData))));
    }
}
