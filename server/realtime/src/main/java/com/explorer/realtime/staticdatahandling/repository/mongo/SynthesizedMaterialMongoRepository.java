package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.SynthesizedMaterial;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SynthesizedMaterialMongoRepository extends ReactiveMongoRepository<SynthesizedMaterial, String> {
}
