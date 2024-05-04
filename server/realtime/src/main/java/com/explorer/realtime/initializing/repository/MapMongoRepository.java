package com.explorer.realtime.initializing.repository;

import com.explorer.realtime.initializing.entity.Map;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapMongoRepository extends ReactiveMongoRepository<Map, Integer> {
}

