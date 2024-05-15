package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.initializing.repository.PlayerPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngameBroadcastPosition {

    private final Broadcasting broadcasting;
    private final CurrentMapRepository currentMapRepository;

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        boolean isNewUser = json.getBoolean("isNewUser");
//        Integer mapId = json.getInt("mapId");
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (isNewUser) {
            log.info("newUser");
            return currentMapRepository.findMapId(channelId)
                    .flatMap(mapId -> {
                        map.put("mapId", mapId);
                        map.put("position", "1:0:1");
                        broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map))).subscribe();
                        return Mono.just(mapId);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                                map.put("mapId", 1);
                                map.put("position", "1:0:1");
                                return broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map)));
                            }))
                    .then();
        } else {
            String position = json.getString("position");
            Integer mapId = json.getInt("mapId");
            map.put("position", position);
            map.put("mapId", mapId);
            broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map))).subscribe();
        }
        return Mono.empty();
    }
}
