package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartGame {

    private final ChannelMongoRepository channelMongoRepository;

    private final RedisTemplate<String, String > redisTemplate;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;


    public void process(String teamCode, String channelName) {
        log.info("Processing game start for teamCode: {}", teamCode);
        String channelId = createChannelId();
        Set<String> userIds = redisTemplate.opsForSet().members("channel:" + teamCode);

        log.info("Redis teamCode {}: {}", teamCode, userIds);

        Set<Long> memberIdsLong = new HashSet<>();
        try{
            for (Object memberId : userIds) {
                memberIdsLong.add(Long.parseLong((String) memberId));
        }
        } catch (NumberFormatException e) {
            log.error("error", e);
            return;
        }
        Channel channel = new Channel(channelId, channelName, memberIdsLong);
        Channel channel1 = channelMongoRepository.save(channel).block();
        log.info("channel : {}", channel1.getId());
        updateConnection(teamCode, channelId).subscribe();
        updateRedis(channelId, new HashSet<>(userIds), teamCode);
    }

    private String createChannelId() {
        ObjectId newChannelId = new ObjectId();
        return newChannelId.toHexString();
    }

    private void updateRedis(String channelId, Set<String> memberIds, String teamCode) {
        String newKey = "channel:" + channelId;

        redisTemplate.opsForSet().add(newKey, memberIds.toArray(new String[0]));
        redisTemplate.delete("channel:" + teamCode);
    }

    private Mono<Void> updateConnection(String teamCode, String channel) {
        log.info("updateconnection", teamCode);
        ReactiveHashOperations<String, String, String> hashOps = reactiveRedisTemplate.opsForHash();
        return hashOps.entries(teamCode)
                .collectList()
                .flatMap(userId -> {
                    if (userId.isEmpty()) {
                        return Mono.error(new RuntimeException("Key not found or no entries in hash"));
                    }
                    return hashOps.putAll(channel, userId.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                            .then(Mono.defer(() -> reactiveRedisTemplate.delete(teamCode)))
                                    .then();
                });
    }
}


