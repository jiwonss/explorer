package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.FarmableCategory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FarmableCategoryMongoRepository extends ReactiveMongoRepository<FarmableCategory, String> {
}
