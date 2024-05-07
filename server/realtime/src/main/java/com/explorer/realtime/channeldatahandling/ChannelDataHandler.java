package com.explorer.realtime.channeldatahandling;

import com.explorer.realtime.channeldatahandling.event.GetChannelList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;


@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelDataHandler {

    private final GetChannelList getChannelList;

    public Mono<Void> channelDataHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getChannelList":
                log.info("event : {}", eventName);
                Long userId = json.getLong("userId");
                getChannelList.process(userId, connection).subscribe();
                break;
        }

        return Mono.empty();
    }

}
