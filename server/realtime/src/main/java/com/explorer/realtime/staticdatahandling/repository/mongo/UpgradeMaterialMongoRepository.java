package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.UpgradeMaterial;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UpgradeMaterialMongoRepository extends ReactiveMongoRepository<UpgradeMaterial, String> {
}
