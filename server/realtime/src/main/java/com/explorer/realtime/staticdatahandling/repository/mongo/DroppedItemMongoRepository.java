package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.DroppedItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DroppedItemMongoRepository extends ReactiveMongoRepository<DroppedItem, String> {
}
