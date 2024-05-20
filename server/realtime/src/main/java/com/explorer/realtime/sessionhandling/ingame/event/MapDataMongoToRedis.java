package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.mongo.repository.MapDataMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapDataMongoToRedis {

    private final MapDataMongoRepository mapDataMongoRepository;
    private final MapObjectRepository mapObjectRepository;

    public Mono<Void> process(String channelId) {
        List<Integer> mapIds = Arrays.asList(1, 2, 3);

        return Flux.fromIterable(mapIds)
                .flatMap(mapId -> mapDataMongoRepository.findByChannelIdAndMapId(channelId, mapId))
                .flatMap(data -> {
                    return Flux.fromIterable(data.getPositions())  // data의 positions 리스트를 처리
                            .flatMap(positionData -> {
                                Integer mapId = data.getMapId();
                                String position = positionData.getPosition();
                                String itemCategory = positionData.getItemCategory();
                                String isFarmable = positionData.getIsFarmable();
                                Integer itemId = positionData.getItemId();
                                return mapObjectRepository.save(channelId, mapId, position, itemCategory, isFarmable, itemId);
                            });
                })
                .then();  // 모든 작업
    }
}
