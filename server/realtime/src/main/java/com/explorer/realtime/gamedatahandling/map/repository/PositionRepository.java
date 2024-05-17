package com.explorer.realtime.gamedatahandling.map.repository;

import com.explorer.realtime.gamedatahandling.map.document.Position;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PositionRepository extends ReactiveMongoRepository<Position, String> {
}
