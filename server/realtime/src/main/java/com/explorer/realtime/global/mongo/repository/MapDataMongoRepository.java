package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.MapData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MapDataMongoRepository extends ReactiveMongoRepository<MapData, String> {
}
