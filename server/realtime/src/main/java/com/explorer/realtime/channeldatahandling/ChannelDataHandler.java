package com.explorer.realtime.channeldatahandling;

import com.explorer.realtime.channeldatahandling.event.GetChannelDetails;
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
    private final GetChannelDetails getChannelDetails;

    public Mono<Void> channelDataHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getChannelList":
                log.info("event : {}", eventName);
                getChannelList.process(json, connection).subscribe();
                break;

            case "getChannelDetails":
                log.info("event : {}", eventName);
                getChannelDetails.process(json, connection).subscribe();
                break;
        }

        return Mono.empty();
    }

}
