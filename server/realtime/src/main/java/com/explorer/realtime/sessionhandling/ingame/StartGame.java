package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import com.explorer.realtime.sessionhandling.repository.ChannelMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartGame {

    private final ChannelMongoRepository channelMongoRepository;

    private final RedisTemplate<String, String > redisListTemplate;


    public void process(String teamCode, String channelName) {
        String channelId = createChannelId();
        List<String> memberIds = redisListTemplate.opsForList().range("teamCode:" + teamCode, 0, -1);

        List<Long> memberIdsLong = new ArrayList<>();
        for (Object memberId : memberIds) {
            memberIdsLong.add(Long.parseLong((String) memberId));
        }
        Channel channel = new Channel(channelId, channelName, memberIdsLong);
        channelMongoRepository.save(channel);
    }

    private String createChannelId() {
        ObjectId newChannelId = new ObjectId();
        return newChannelId.toHexString();
    }

}


