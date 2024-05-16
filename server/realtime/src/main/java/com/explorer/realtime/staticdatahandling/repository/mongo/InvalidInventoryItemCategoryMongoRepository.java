package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.InvalidInventoryItemCategory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface InvalidInventoryItemCategoryMongoRepository extends ReactiveMongoRepository<InvalidInventoryItemCategory, String> {
}
