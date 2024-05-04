package com.explorer.realtime.initializing;

import com.explorer.realtime.initializing.entity.Map;
import com.explorer.realtime.initializing.repository.MapMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeHandler {

    private final MapMongoRepository mapMongoRepository;

    public Mono<Map> initializeHandler(JSONObject json) {
        Integer mapId = json.getInt("mapId");
        JSONArray positionsJson = json.getJSONArray("positions");
        Set<String> positions = new HashSet<>();
        for (int i = 0; i < positionsJson.length(); i++) {
            positions.add(positionsJson.getString(i));
        }
        log.info("initializeHandler: Processing mapId = {}, position = {}", mapId, positions);

        return mapMongoRepository.findById(mapId)
                .flatMap(existingMap -> {
                    existingMap.getPosition().addAll(positions);
                    return mapMongoRepository.save(existingMap);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Map newMap = new Map(mapId, positions);
                    return mapMongoRepository.save(newMap);
                }
            )
        );
    }
}
