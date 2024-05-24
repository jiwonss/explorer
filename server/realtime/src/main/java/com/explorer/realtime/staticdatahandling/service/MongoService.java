package com.explorer.realtime.staticdatahandling.service;

import com.explorer.realtime.staticdatahandling.document.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MongoService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<DroppedItem> findAllDroppedItem() {
        return reactiveMongoTemplate.findAll(DroppedItem.class);
    }

    public Flux<ExtractionMaterial> findAllExtractionMaterial() {
        return reactiveMongoTemplate.findAll(ExtractionMaterial.class);
    }

    public Flux<FarmableCategory> findAllFarmableCategory() {
        return reactiveMongoTemplate.findAll(FarmableCategory.class);
    }

    public Flux<Item> findAllItem() {
        return reactiveMongoTemplate.findAll(Item.class);
    }

    public Flux<LabEfficiency> findAllLabEfficiency() {
        return reactiveMongoTemplate.findAll(LabEfficiency.class);
    }

    public Flux<SynthesizedMaterial> findAllSynthesizedMaterial() {
        return reactiveMongoTemplate.findAll(SynthesizedMaterial.class);
    }

    public Flux<UpgradeMaterial> findAllUpgradeMaterial() {
        return reactiveMongoTemplate.findAll(UpgradeMaterial.class);
    }

    public Flux<Position> findPositionByMapId(int mapId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mapId").is(mapId));
        return reactiveMongoTemplate.find(query, Position.class);
    }

}
