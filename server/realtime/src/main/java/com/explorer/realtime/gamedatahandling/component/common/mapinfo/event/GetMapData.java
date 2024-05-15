package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetMapData {

    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;
    private final CurrentMapRepository currentMapRepository;
    private final ChannelRepository channelRepository;

    public Mono<Void> getMapData(String channelId, Integer mapId) {
        return mapObjectRepository.findMapData(channelId, mapId)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("getMapData", CastingType.BROADCASTING, mapData))))
                .then(currentMapRepository.save(channelId, mapId))
                .then(position(channelId, mapId));
    }

    private Mono<Void> position(String channelId, Integer mapId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(field -> {
                    Long userId = Long.parseLong(String.valueOf(field));
                    String position = getNewPosition(userId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("position", position);
                    map.put("userId", userId);
                    map.put("mapId", mapId);
                    return broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("startGame", CastingType.BROADCASTING, map)));
                }).then();
    }

    private String getNewPosition(Long userId) {
        String[] positions = {"1:0:1", "2:0:2", "3:0:3", "1:0:2", "2:0:3", "1:0:3"};
        int index = Math.abs(userId.hashCode()) % positions.length;
        return positions[index];
    }
}
