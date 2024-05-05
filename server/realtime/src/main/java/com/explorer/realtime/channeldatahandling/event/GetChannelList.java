package com.explorer.realtime.channeldatahandling.event;

import com.explorer.realtime.channeldatahandling.client.UserClient;
import com.explorer.realtime.channeldatahandling.service.ChannelService;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetChannelList {

    private final UserClient userClient;
    private final ChannelService channelService;
    private static final String TOKEN_PREFIX = "Bearer ";

    public Mono<Void> process(String accessToken) {
        log.info("getChannelList - accessToken : {}", accessToken);
        return Mono.empty();
    }

    public Mono<Void> process(Long userId, Connection connection) {
        log.info("getChannelList - userId : {}", userId);

        channelService.findChannelInfoByUserId(userId).subscribe(
                channels -> {
                    log.info("channels : {}", channels);
                    unicasting(
                            connection,
                            userId,
                            MessageConverter.convert(
                                    Message.success("getChannelList", CastingType.UNICASTING, channels)
                            )
                    ).subscribe();
                },
                error -> {
                    log.info("error : findChannelsByUserId");
                }
        );

        return Mono.empty();
    }

    private Mono<Void> unicasting(Connection connection, Long userId, JSONObject msg) {
        log.info("getChannelList unicasting");

        if (connection == null) {
            log.warn("No connection found for {}", userId);
            return Mono.empty();
        }

        return connection.outbound().sendString(Mono.just(msg.toString()+'\n'))
                .then()
                .doOnSuccess(aVoid -> log.info("Unicast completed : {}", userId))
                .doOnError(error -> log.error("Unicast failed for userId: {}, error: {}", userId, error.getMessage()));
    }

}
