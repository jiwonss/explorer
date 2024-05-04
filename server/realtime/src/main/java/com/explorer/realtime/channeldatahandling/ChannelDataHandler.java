package com.explorer.realtime.channeldatahandling;

import com.explorer.realtime.channeldatahandling.event.GetChannelList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelDataHandler {

    private final GetChannelList getChannelList;

    public Mono<Void> channelDataHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getChannelList":
                log.info("event : {}", eventName);
                String accessToken = json.getString("accessToken");
                getChannelList.process(accessToken);
                break;
        }

        return Mono.empty();
    }

}
