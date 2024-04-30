package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartGame {

    private final ChannelMongoRepository channelMongoRepository;

    private final RedisTemplate<String, String > redisListTemplate;


    public void process(String teamCode, String channelName) {
        String channelId = createChannelId();
        List<String> memberIds = redisListTemplate.opsForList().range("channel:" + teamCode, 0, -1);

        List<Long> memberIdsLong = new ArrayList<>();
        for (Object memberId : memberIds) {
            memberIdsLong.add(Long.parseLong((String) memberId));
        }
        Channel channel = new Channel(channelId, channelName, memberIdsLong);
        Channel channel1 = channelMongoRepository.save(channel);
        log.info("channel : {}", channel1.getId());
        updateRedis(channelId, memberIds, teamCode);
    }

    private String createChannelId() {
        ObjectId newChannelId = new ObjectId();
        return newChannelId.toHexString();
    }

    private void updateRedis(String channelId, List<String> memberIds, String teamCode) {
        String newKey = "channel:" + channelId;

        redisListTemplate.opsForList().rightPushAll(newKey, memberIds.toArray(new String[0]));
        redisListTemplate.delete("channel:" + teamCode);
    }

}


