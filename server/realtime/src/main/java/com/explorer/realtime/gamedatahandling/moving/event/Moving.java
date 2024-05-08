package com.explorer.realtime.gamedatahandling.moving.event;

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
public class Moving {

    private final Broadcasting broadcasting;

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        Long userId = json.getLong("userId");
        String position = json.getString("position");

        Map<String, Object> map = new HashMap<>();
        map.put("mapId", mapId);
        map.put("userId", userId);
        map.put("position", position);

        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success("moving", CastingType.BROADCASTING, map))
        ).then();
    }

}
