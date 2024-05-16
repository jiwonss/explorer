package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastPosition {

    private final ChannelRepository channelRepository;
    private final Broadcasting broadcasting;

    private static final String eventName = "broadcastPosition";
    private static final String[] positions = {"1:0:1", "2:0:2", "3:0:3", "1:0:2", "2:0:3", "1:0:3"};

    public Mono<Void> process(JSONObject json) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        boolean isNewUser = json.getBoolean("isNewUser");

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        Mono<Map<String, Object>> mapMono;

        if (isNewUser) {
            log.info("[process] teamCode : {}, userId : {}, isNewUser : {}", teamCode, userId, isNewUser);
            mapMono = channelRepository.count(teamCode)
                    .flatMap(count -> {
                        int idx = Integer.parseInt(String.valueOf(count)) - 1;
                        map.put("position", positions[idx]);
                        return Mono.just(map);
                    });
        } else {
            String position = json.getString("position");
            log.info("[process] teamCode : {}, userId : {}, isNewUser : {}, position : {}", teamCode, userId, isNewUser, position);
            map.put("position", position);
            mapMono = Mono.just(map);
        }

        return mapMono.flatMap(finalMap -> {
            log.info("[process] map : {}", finalMap);
            return broadcasting.broadcasting(
                    teamCode,
                    MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, finalMap))
            ).then();
        });
    }

}
