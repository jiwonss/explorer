package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.mongo.entity.MapData;
import com.explorer.realtime.global.mongo.entity.PositionData;
import com.explorer.realtime.global.mongo.repository.MapDataMongoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnMainMap {

    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;
    private final MapDataMongoRepository mapDataMongoRepository;
    private final CurrentMapRepository currentMapRepository;
    private final ChannelRepository channelRepository;

    public Mono<Void> returnMainMap(JSONObject json) {
        String channelId = json.getString("channelId");
        Integer mapId = json.getInt("mapId");
        log.info("returnMain start");
        if (mapId == 4) {
            mapObjectRepository.resetMapData(channelId, mapId).subscribe();
        } else { // else mongodb에 데이터 저장하기
            log.info("returnMap else");
            return mapObjectRepository.findMapData(channelId, mapId)
                    .flatMap(data -> {
                        log.info("mapRepository {}", data);
                        MapData mapData = new MapData();
                        mapData.setChannelId(channelId);
                        mapData.setMapId(mapId);
                        mapData.setPositions(data.entrySet().stream()
                                .map(entry -> createPositionData(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList()));
                        log.info("mongo save logic");
                        mapDataMongoRepository.save(mapData).subscribe();
                        currentMapRepository.save(channelId, 1).subscribe();
                        position(channelId).subscribe();
                        return mapObjectRepository.findMapData(channelId, 1)
                                .flatMap(mainMapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("returnMainMap", CastingType.BROADCASTING, mainMapData))));

                    });
        }
        log.info("before broadcasting");
        return mapObjectRepository.findMapData(channelId, 1)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("returnMainMap", CastingType.BROADCASTING, mapData))));
    }

    private PositionData createPositionData(String position, String value) {
        String[] parts = value.split(":");
        return new PositionData(position, parts[0], parts[1], Integer.parseInt(parts[2]));
    }

    private Mono<Void> position(String channelId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(field -> {
                    Long userId = Long.parseLong(String.valueOf(field));
                    String position = getNewPosition(userId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("position", position);
                    map.put("userId", userId);
                    map.put("mapId", 1);
                    return broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("startGame", CastingType.BROADCASTING, map)));
                }).then();
    }

    private String getNewPosition(Long userId) {
        String[] positions = {"1:0:1", "2:0:2", "3:0:3", "1:0:2", "2:0:3", "1:0:3"};
        int index = Math.abs(userId.hashCode()) % positions.length;
        return positions[index];
    }
}
