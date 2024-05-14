package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemMongoRepository extends ReactiveMongoRepository<Item, String> {
}
