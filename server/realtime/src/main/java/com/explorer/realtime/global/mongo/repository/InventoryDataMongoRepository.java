package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.Inventory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface InventoryDataMongoRepository extends ReactiveMongoRepository<Inventory, String> {
}
