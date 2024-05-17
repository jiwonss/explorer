package com.explorer.realtime.gamedatahandling.map.event;

import com.explorer.realtime.gamedatahandling.map.repository.PositionRepository;
import com.explorer.realtime.gamedatahandling.map.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavePositionsToMongoDB {

    private final PositionService positionService;
    private final PositionRepository positionRepository;


    public Mono<Void> process(JSONObject json) {
        int mapId = json.getInt("mapId");
        JSONArray positions = json.getJSONArray("positions");
        log.info("[process] mapId : {}, positions : {}", mapId, positions);

        return positionService.findByMapId(mapId)
                .flatMap(map -> {
                    log.info("[process] map : {}", map);
                    positions.forEach(position -> {
                        map.addPosition(String.valueOf(position));
                    });
                    return positionRepository.save(map);
                }).then();
    }

}
