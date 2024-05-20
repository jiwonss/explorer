package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.AvailableInventoryItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AvailableInventoryItemMongoRepository extends ReactiveMongoRepository<AvailableInventoryItem, String> {
}
