package com.explorer.realtime.initializing.event;

import com.explorer.realtime.initializing.entity.Map;
import com.explorer.realtime.initializing.repository.MapMongoRepository;
import com.explorer.realtime.initializing.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialMapRedis {

    private final MapMongoRepository mapMongoRepository;
    private final MapRepository mapRepository;

    public Flux<Boolean> initialMapRedis(){
        return mapMongoRepository.findAll()
                .flatMap(this::saveMapToRedis);
    }

    private Mono<Boolean> saveMapToRedis(Map map) {
        Integer mapId = map.getMapId();
        String positions = map.getPosition().toString();
        return mapRepository.saveMapData(mapId, positions);
    }
}
