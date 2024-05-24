package com.explorer.realtime.staticdatahandling.event;

import com.explorer.realtime.staticdatahandling.document.Position;
import com.explorer.realtime.staticdatahandling.repository.mongo.PositionMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveNewPositionsToMongoDB {

    private final PositionMongoRepository positionMongoRepository;

    public Mono<Void> process(JSONObject json) {
        int mapId = json.getInt("mapId");
        JSONArray positions = json.getJSONArray("positions");
        log.info("[process] mapId : {}, positions : {}", mapId, positions);

        Set<String> positionsSet = IntStream.range(0, positions.length())
                .mapToObj(positions::getString)
                .collect(Collectors.toSet());
        return positionMongoRepository.save(Position.from(mapId, positionsSet)).then();
    }

}
