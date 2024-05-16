package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.util.MessageConverter;
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

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        String position = json.getString("position");
        Integer mapId = json.getInt("mapId");
        map.put("position", position);
        map.put("mapId", mapId);
        broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map))).subscribe();
        return Mono.empty();
    }
}
