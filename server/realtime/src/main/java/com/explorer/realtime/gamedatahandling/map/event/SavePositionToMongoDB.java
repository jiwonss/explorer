package com.explorer.realtime.gamedatahandling.map.event;

import com.explorer.realtime.gamedatahandling.map.repository.PositionRepository;
import com.explorer.realtime.gamedatahandling.map.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavePositionToMongoDB {

    private final PositionService positionService;
    private final PositionRepository positionRepository;

    public Mono<Void> process(JSONObject json) {
        int mapId = json.getInt("mapId");
        String position = json.getString("position");
        log.info("[process] mapId : {}, position : {}", mapId, position);

        return positionService.findByMapId(mapId)
                .flatMap(map -> {
                    log.info("[process] map : {}", map);
                    map.addPosition(position);
                    return positionRepository.save(map);
                })
                .then();
    }

}
