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
    private final Unicasting unicasting;

    private static final String TOKEN_PREFIX = "Bearer ";

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return channelService.findAllChannelInfoByUserId(userId)
                .doOnNext(channels -> {
                    log.info("[process] channels : {}", channels);
                    unicasting.unicasting(
                            connection,
                            userId,
                            MessageConverter.convert(
                                    Message.success("getChannelList", CastingType.UNICASTING, channels)
                            )
                    ).subscribe();
                })
                .onErrorResume(error -> {
                    return Mono.empty();
                })
                .then();
    }

}
