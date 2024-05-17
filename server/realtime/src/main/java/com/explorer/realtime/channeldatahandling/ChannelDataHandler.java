package com.explorer.realtime.channeldatahandling;

import com.explorer.realtime.channeldatahandling.event.DeleteChannel;
import com.explorer.realtime.channeldatahandling.event.GetChannelDetails;
import com.explorer.realtime.channeldatahandling.event.GetChannelList;
import com.explorer.realtime.channeldatahandling.event.GetEndedChannelList;
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
    private final GetEndedChannelList getEndedChannelList;
    private final DeleteChannel deleteChannel;

    public Mono<Void> channelDataHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getChannelList":
                log.info("eventName : {}", eventName);
                getChannelList.process(json, connection).subscribe();
                break;

            case "getChannelDetails":
                log.info("eventName : {}", eventName);
                getChannelDetails.process(json, connection).subscribe();
                break;

            case "getEndedChannelList":
                log.info("eventName : {}", eventName);
                getEndedChannelList.process(json, connection).subscribe();
                break;

            case "deleteChannel":
                log.info("eventName : {}", eventName);
                deleteChannel.process(json, connection).subscribe();
                break;
        }

        return Mono.empty();
    }

}
