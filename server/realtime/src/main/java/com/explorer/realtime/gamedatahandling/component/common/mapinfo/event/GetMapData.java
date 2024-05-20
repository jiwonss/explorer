package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetMapData {

    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;
    private final CurrentMapRepository currentMapRepository;

    public Mono<Void> getMapData(String channelId, Integer mapId) {
        return mapObjectRepository.findMapData(channelId, mapId)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("getMapData", CastingType.BROADCASTING, mapData))))
                .then(currentMapRepository.save(channelId, mapId))
                .then();
    }
}
