package com.explorer.realtime.staticdatahandling.repository.mongo;

import com.explorer.realtime.staticdatahandling.document.Position;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PositionMongoRepository extends ReactiveMongoRepository<Position, String> {
}
