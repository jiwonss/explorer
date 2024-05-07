package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.initializing.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeMapObject {

    private final MapRepository mapRepository;
    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;


    public Mono<Void> initializeMapObject(String channelId) {
        Integer mapId = 1;
        Integer category = 1;
        Integer itemId = 1;
        log.info("Initializing Map Object for channelId: {}, mapId: {}", channelId, mapId);

        return mapRepository.findMapData(mapId)
//                .map(data -> cleanData(data))  // 데이터에서 불필요한 문자 제거
                .collectList()
                .flatMap(data -> {
                    List<String> selectedData = selectRandomEntries(data, 50);
                    return mapObjectRepository.saveMapData(channelId, mapId, selectedData, category, itemId);
                })
                .flatMap(result -> {
                    if (result) {
                        return mapObjectRepository.findMapData(channelId, mapId)
                                .flatMap(mapData -> broadcastMapData(channelId, mapData, mapId))
                                .doOnSuccess(avoid -> log.info("Map data broadcasted successfully for channelId: {}, mapId: {}", channelId, mapId))
                                .doOnError(error -> log.error("Failed to broadcast map data for channelId: {}, mapId: {}", channelId, mapId));
                    } else {
                        log.error("Failed to save map data for channelId: {}, mapId: {}", channelId, mapId);
                        return Mono.error(new RuntimeException("Failed to save map data"));
                    }
                });
    }

    private List<String> selectRandomEntries(List<String> positions, int count) {
        List<String> selectedEntries = new ArrayList<>(positions);
        Collections.shuffle(selectedEntries);  // 리스트를 무작위로 섞음
        return selectedEntries.subList(0, Math.min(selectedEntries.size(), count));  // 랜덤하게 count개의 요소를 선택
    }

    private Mono<Void> broadcastMapData(String channelId, Map<String, String> mapData, Integer mapId) {
        JSONObject msg = new JSONObject();
        JSONObject dataHeader = new JSONObject();
        dataHeader.put("eventName", "initializeMap");
        dataHeader.put("castingType", "BROADCASTING");
        dataHeader.put("msg", "success");

        JSONObject dataBody = new JSONObject();
        dataBody.put("mapId", mapId);

        JSONObject pos = new JSONObject();
        for (Map.Entry<String, String> entry : mapData.entrySet()) {
            pos.put(entry.getKey(), entry.getValue());
        }
        dataBody.put("pos", pos);

        msg.put("dataHeader", dataHeader);
        msg.put("dataBody", dataBody);

        return broadcasting.broadcasting(channelId, msg);
    }
}
