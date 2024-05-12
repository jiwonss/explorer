package com.explorer.realtime.global.mongo.repository;

import com.explorer.realtime.global.mongo.entity.Laboratory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LaboratoryDataMongoRepository extends ReactiveMongoRepository<Laboratory, String> {
}
