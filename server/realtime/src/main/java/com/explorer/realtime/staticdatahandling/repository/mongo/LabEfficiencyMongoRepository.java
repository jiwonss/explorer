package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.LabEfficiency;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LabEfficiencyMongoRepository extends ReactiveMongoRepository<LabEfficiency, String> {
}
