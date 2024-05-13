package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.Laboratory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LaboratoryDataMongoRepository extends ReactiveMongoRepository<Laboratory, String> {
    Mono<Laboratory> findByChannelIdAndItemCategory(String channelId, String itemCategory);
}
