package com.explorer.realtime.staticdatahandling.event;

import com.explorer.realtime.staticdatahandling.repository.redis.MapDataRepository;
import com.explorer.realtime.staticdatahandling.service.MongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveMapDataToRedis {

    private final MongoService mongoService;
    private final MapDataRepository mapDataRepository;

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        log.info("[process] channelId : {}, mapId : {}", channelId, mapId);

        return save(channelId, mapId).then();
    }

    public Mono<Void> save(String channelId, int mapId) {
        return mongoService.findPositionByMapId(mapId)
                .flatMap(positionInfo -> {
                    log.info("[process] positionInfo : {}", positionInfo);
                    return Flux.fromIterable(positionInfo.getPositions())
                            .flatMap(position -> {
                                String[] info = position.split(":");
                                String pos = info[0] + ":" + info[1] + ":" + info[2];
                                String itemCategory = info[3];
                                int itemId = Integer.parseInt(info[4]);
                                return mapDataRepository.save(channelId, mapId, pos, itemCategory, itemId);
                            });
                })
                .then();
    }

}
