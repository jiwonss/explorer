package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.MapData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MapDataMongoRepository extends ReactiveMongoRepository<MapData, String> {
    Flux<MapData> findByChannelIdAndMapId(String channelId, Integer mapId);
}
