package com.explorer.realtime.channeldatahandling.event;

import com.explorer.realtime.channeldatahandling.dto.ChannelDetailsInfo;
import com.explorer.realtime.channeldatahandling.service.ChannelService;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetChannelDetails {

    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final Unicasting unicasting;

    private static final String eventName = "getChannelDetails";

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        String channelId = json.getString("channelId");
        log.info("[process] channelId : {}, connection : {}", channelId, connection);

        return getChannelDetails(channelId)
                .doOnNext(channelDetailsInfoList -> {
                    log.info("[process] channelDetailsInfoList : {}", channelDetailsInfoList);
                    unicasting.unicasting(
                            connection,
                            userId,
                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, channelDetailsInfoList))
                    ).subscribe();
                })
                .then();
    }

    private Mono<List<ChannelDetailsInfo>> getChannelDetails(String channelId) {
        log.info("[getChannelDetails] channelId : {}", channelId);

        return channelService.findPlayerListByChannelId(channelId)
                .flatMap(playerId -> {
                    log.info("[getChannelDetails] playerId : {}", playerId);
                    return channelRepository.existByUserId(channelId, playerId)
                            .map(exists ->ChannelDetailsInfo.of(playerId, exists));
                })
                .collectList();
    }

}
