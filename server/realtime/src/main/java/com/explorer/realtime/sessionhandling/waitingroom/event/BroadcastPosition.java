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

    public Mono<Void> process(String teamCode, Long userId, boolean isNewUser, JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (isNewUser) {
            map.put("position", "0:0:0:0:0:0");
        } else {
            String position = json.getString("position");
            map.put("position", position);
        }

        broadcasting.broadcasting(
                teamCode,
                MessageConverter.convert(Message.success("broadcastPosition", CastingType.BROADCASTING, map))
        ).subscribe();
        return Mono.empty();
    }

}
