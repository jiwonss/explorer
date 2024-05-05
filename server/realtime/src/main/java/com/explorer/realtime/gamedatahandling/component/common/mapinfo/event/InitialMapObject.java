package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.initializing.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitialMapObject {

    private final MapRepository mapRepository;
    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;

    public Mono<String> initialMapObject(String channelId) {
        Integer mapId = 1;
        Integer numberOfPositions = 150;
        return mapRepository.findMapData(mapId)
                .map(data -> convertDataToClientFormat(channelId, mapId, data, numberOfPositions))
                .flatMap(data -> {
                    JSONObject msg = new JSONObject();
                    msg.put("mapData", data);
                    mapObjectRepository.saveMapData(channelId, mapId, data).subscribe();
                    broadcasting.broadcasting(channelId, msg);
                    return Mono.empty();
                });
    }

    private String convertDataToClientFormat(String channelId, Integer mapId, String rawData, int numberOfPositions) {
//        String[] positions = rawData.split(", ");
        List<String> positions = Arrays.asList(rawData.split(", "));
        Collections.shuffle(positions);
        positions = positions.subList(0, Math.min(positions.size(), numberOfPositions));
        StringBuilder formatteDate = new StringBuilder();
        for (String position : positions) {
            formatteDate.append(formatPositionData(channelId, mapId, position.trim())).append(", ");
        }
        return formatteDate.toString();
    }

    private String formatPositionData(String channelId, Integer mapId, String positionData) {
        String[] parts = positionData.split(":");
        return String.format("%s:%d:mapInfo %s:%s:%s:%s:%s:%s %s:%s", channelId, mapId,
                parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], "category", "itemId");
    }
}
