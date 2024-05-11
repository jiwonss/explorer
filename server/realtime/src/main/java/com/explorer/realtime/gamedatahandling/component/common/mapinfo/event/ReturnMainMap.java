package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.mongo.entity.MapData;
import com.explorer.realtime.global.mongo.entity.PositionData;
import com.explorer.realtime.global.mongo.repository.MapDataMongoRepository;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnMainMap {

    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;
    private final MapDataMongoRepository mapDataMongoRepository;

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
                        return Mono.empty();
                    });
        }
        return mapObjectRepository.findMapData(channelId, 1)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("returnMainMap", CastingType.BROADCASTING, mapData))));
    }

    private PositionData createPositionData(String position, String value) {
        String[] parts = value.split(":");
        return new PositionData(position, parts[0], parts[1], Integer.parseInt(parts[2]));
    }
}
