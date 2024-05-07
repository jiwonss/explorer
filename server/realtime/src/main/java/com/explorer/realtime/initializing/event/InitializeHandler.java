package com.explorer.realtime.initializing.event;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeHandler {

    private final MapMongoRepository mapMongoRepository;

    public Mono<Map> initializeHandler(JSONObject json) {
        Integer mapId = json.getInt("mapId");

        Set<String> positions = IntStream.range(0, json.getJSONArray("positions").length())
        .mapToObj(index -> json.getJSONArray("positions").getString(index))
        .collect(Collectors.toCollection(HashSet::new));
        log.info("initializeHandler: Processing mapId = {}, position = {}", mapId, positions);

        return mapMongoRepository.findById(mapId)
                .flatMap(existingMap -> {
                    existingMap.getPosition().clear();
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
