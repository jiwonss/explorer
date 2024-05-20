package com.explorer.realtime.staticdatahandling.event;

import com.explorer.realtime.staticdatahandling.repository.mongo.PositionMongoRepository;
import com.explorer.realtime.staticdatahandling.service.MongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavePositionToMongoDB {

    private final MongoService mongoService;
    private final PositionMongoRepository positionMongoRepository;

    public Mono<Void> process(JSONObject json) {
        int mapId = json.getInt("mapId");
        String position = json.getString("position");
        String itemCategory = json.getString("itemCategory");
        int itemId = json.getInt("itemId");
        log.info("[process] mapId : {}, position : {}, itemCategory : {}, itemId : {}", mapId, position, itemCategory, itemId);

        return mongoService.findPositionByMapId(mapId)
                .flatMap(map -> {
                    log.info("[process] map : {}", map);
                    String value = position + ":" + itemCategory + ":" + String.valueOf(itemId);
                    map.addPosition(value);
                    return positionMongoRepository.save(map);
                })
                .then();
    }

}
