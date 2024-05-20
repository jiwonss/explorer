package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.Inventory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface InventoryDataMongoRepository extends ReactiveMongoRepository<Inventory, String> {
    Flux<Inventory> findByChannelIdAndUserId(String channelId, Long userId);
}
