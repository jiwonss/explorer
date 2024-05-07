package com.explorer.realtime.initializing.event;

import com.explorer.realtime.initializing.entity.Map;
import com.explorer.realtime.initializing.repository.MapMongoRepository;
import com.explorer.realtime.initializing.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialMapRedis {

    private final MapMongoRepository mapMongoRepository;
    private final MapRepository mapRepository;

    public Flux<Long> initializeMapRedis(){
        return mapMongoRepository.findAll()
                .flatMap(this::saveMapToRedis)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No maps found in MongoDB");
                    return Mono.empty();
                }));
    }

    private Mono<Long> saveMapToRedis(Map map) {
        Integer mapId = map.getMapId();
        Set<String> positionSet = map.getPosition();
        List<String> positions = new ArrayList<>(positionSet);
        return mapRepository.saveMapData(mapId, positions);
    }
}
