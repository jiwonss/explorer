package com.explorer.realtime.sessionhandling.waitingroom.event;

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
public class BroadcastPosition {

    private final Broadcasting broadcasting;

    public Mono<Void> process(JSONObject json) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        boolean isNewUser = json.getBoolean("isNewUser");

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (isNewUser) {
            log.info("[process] teamCode : {}, userId : {}, isNewUser : {}", teamCode, userId, isNewUser);
            map.put("position", "0:0:0:0:0:0");
        } else {
            String position = json.getString("position");
            log.info("[process] teamCode : {}, userId : {}, isNewUser : {}, position : {}", teamCode, userId, isNewUser, position);
            map.put("position", position);
        }
        log.info("[process] map : {}", map);

        return broadcasting.broadcasting(
                teamCode,
                MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map))
        ).then();
    }

}
