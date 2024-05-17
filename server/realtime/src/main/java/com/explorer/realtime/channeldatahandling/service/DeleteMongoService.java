package com.explorer.realtime.channeldatahandling.service;

import com.explorer.realtime.global.mongo.entity.Inventory;
import com.explorer.realtime.global.mongo.entity.Laboratory;
import com.explorer.realtime.global.mongo.entity.MapData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteMongoService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Void> deleteInventoryByChannelIdAndUserId(String channelId, Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("channelId").is(channelId).and("userId").is(userId));
        return reactiveMongoTemplate.remove(query, Inventory.class).then();
    }

    public Mono<Void> deleteLaboratoryByChannelId(String channelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("channelId").is(channelId));
        return reactiveMongoTemplate.remove(query, Laboratory.class).then();
    }

    public Mono<Void> deleteMapDataByChannelId(String channelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("channelId").is(channelId));
        return reactiveMongoTemplate.remove(query, MapData.class).then();
    }


}
