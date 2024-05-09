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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWaitingRoomHeadcount {

    private final ChannelRepository channelRepository;
    private final Broadcasting broadcasting;

    private static final String eventName = "getWaitingRoomHeadcount";

    public Mono<Void> process(JSONObject json) {
        String teamCode = json.getString("teamCode");
        log.info("[process] teamCode : {}", teamCode);

        return channelRepository.count(teamCode)
                .doOnNext(headcount -> {
                    log.info("[process] headcount : {}", headcount);

                    Map<String, Object> map = new HashMap<>();
                    map.put("headcount", headcount);

                    broadcasting.broadcasting(
                            teamCode,
                            MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
                    ).subscribe();
                })
                .then();
    }
}
