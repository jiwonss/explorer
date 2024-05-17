package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.Laboratory;
import com.explorer.realtime.global.mongo.entity.LaboratoryLevel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface LaboratoryLevelMongoRepository extends ReactiveMongoRepository<LaboratoryLevel, String> {
    Mono<LaboratoryLevel> findByChannelId(String channelId);
}
