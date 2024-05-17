package com.explorer.realtime.channeldatahandling.service;

import com.explorer.realtime.channeldatahandling.dto.ChannelDetailsInfo;
import com.explorer.realtime.channeldatahandling.dto.ChannelInfo;
import com.explorer.realtime.channeldatahandling.dto.EndedChannelInfo;
import com.explorer.realtime.sessionhandling.ingame.document.Channel;
import com.explorer.realtime.sessionhandling.ingame.dto.UserInfo;
import com.explorer.realtime.sessionhandling.ingame.enums.Status;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<List<ChannelInfo>> findAllInProgressChannelInfoByUserId(Long userId) {
        ProjectionOperation project = Aggregation.project()
                .andExpression("_id").as("channelId")
                .andExpression("name").as("channelName");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("playerList.userId").is(userId)),
                Aggregation.match(Criteria.where("status").is(Status.IN_PROGRESS)),
                project
        );

        return reactiveMongoTemplate.aggregate(aggregation, "channels", ChannelInfo.class)
                .collectList();
    }

    public Mono<List<EndedChannelInfo>> findAllEndedChannelInfoByUserId(Long userId) {
        ProjectionOperation project = Aggregation.project()
                .andExpression("_id").as("channelId")
                .andExpression("name").as("channelName")
                .andExpression("image").as("image")
                .and(DateOperators.DateToString.dateOf("createdAt").toString("%Y/%m/%d %H:%M")).as("createdAt")
                .and(DateOperators.DateToString.dateOf("updatedAt").toString("%Y/%m/%d %H:%M")).as("endedAt")
                .and("playerList.nickname").as("playerList");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("playerList.userId").is(userId)),
                Aggregation.match(Criteria.where("status").is(Status.ENDED)),
                project
        );

        return reactiveMongoTemplate.aggregate(aggregation, "channels", EndedChannelInfo.class)
                .collectList();
    }

    public Mono<ChannelDetailsInfo> findChannelDetailsInfoByChannelId(String channelId) {
        Query query = new Query(Criteria.where("_id").is(channelId));

        Mono<Channel> channelMono = reactiveMongoTemplate.findOne(query, Channel.class);

        Mono<Integer> playerListSizeMono = channelMono
                .map(channel -> channel.getPlayerList().size());

        Mono<LocalDateTime> createdAtMono = channelMono
                .map(Channel::getCreatedAt);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return Mono.zip(playerListSizeMono, createdAtMono)
                .map(tuple -> ChannelDetailsInfo.of(tuple.getT1(), tuple.getT2().format(formatter)));
    }

    public Flux<UserInfo> findPlayerListByChannelId(String channelId) {
        Query query = new Query(Criteria.where("_id").is(channelId));
        return reactiveMongoTemplate.findOne(query, Channel.class)
                .flatMapMany(channel -> Flux.fromIterable(channel.getPlayerList()));
    }

    public Flux<Integer> countPlayerListByChannelId(String channelId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(channelId)),
                Aggregation.project().and(ArrayOperators.Size.lengthOfArray("playerList")).as("cnt")
        );

        return reactiveMongoTemplate.aggregate(aggregation, "channels", Document.class)
                .map(result -> result.getInteger("cnt"));
    }

    public Mono<Void> deleteUserInfoByChannelId(String channelId, Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(channelId));
        query.addCriteria(Criteria.where("playerList.userId").is(userId));

        Update update = new Update();
        update.pull("playerList", new Query(Criteria.where("userId").is(userId)));

        return reactiveMongoTemplate.updateFirst(query, update, Channel.class)
                .then();
    }

    public Mono<Void> deleteChannelByChannelId(String channelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(channelId));

        return reactiveMongoTemplate.remove(query, Channel.class)
                .then();
    }

}
