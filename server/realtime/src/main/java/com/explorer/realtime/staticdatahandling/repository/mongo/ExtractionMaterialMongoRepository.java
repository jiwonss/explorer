package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.ExtractionMaterial;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ExtractionMaterialMongoRepository extends ReactiveMongoRepository<ExtractionMaterial, String> {
}
