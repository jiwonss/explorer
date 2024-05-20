package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.NonDiscardableInventoryItemCategory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NonDiscardableInventoryItemCategoryMongoRepository extends ReactiveMongoRepository<NonDiscardableInventoryItemCategory, String> {
}
