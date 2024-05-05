package com.explorer.realtime.channeldatahandling.service;

import com.explorer.realtime.channeldatahandling.dto.ChannelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<List<ChannelInfo>> findChannelInfoByUserId(Long userId) {
        ProjectionOperation project = Aggregation.project()
                .andExpression("_id").as("channelId")
                .andExpression("name").as("channelName")
                .andExpression("createdAt").as("createdAt");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("playerList").is(userId)),
                project
        );

        return reactiveMongoTemplate.aggregate(aggregation, "channels", ChannelInfo.class)
                .collectList();
    }

}
