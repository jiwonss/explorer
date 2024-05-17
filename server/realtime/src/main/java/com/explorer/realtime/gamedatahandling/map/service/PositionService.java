package com.explorer.realtime.gamedatahandling.map.service;


import com.explorer.realtime.gamedatahandling.map.document.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<Position> findByMapId(int mapId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mapId").is(mapId));
        return reactiveMongoTemplate.find(query, Position.class);
    }


}
