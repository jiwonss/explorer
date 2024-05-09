package com.explorer.realtime.channeldatahandling.event;

import com.explorer.realtime.channeldatahandling.dto.ChannelDetailsInfo;
import com.explorer.realtime.channeldatahandling.dto.PlayerInfo;
import com.explorer.realtime.channeldatahandling.service.ChannelService;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetChannelDetails {

    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Unicasting unicasting;

    private static final String eventName = "getChannelDetails";

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        String channelId = json.getString("channelId");
        log.info("[process] channelId : {}, connection : {}", channelId, connection);

        Mono<ChannelDetailsInfo> channelDetailsMono = getChannelDetailsInfo(channelId);
        Mono<List<PlayerInfo>> playerInfoListMono = getPlayerInfoList(channelId, userId);

        return Mono.zip(channelDetailsMono, playerInfoListMono)
                .flatMap(result -> {
                    log.info("[process] result : {}", result);

                    Map<String, Object> map = new HashMap<>();
                    map.put("channelDetailsInfo", result.getT1());
                    map.put("playerInfoList", result.getT2());

                    return unicasting.unicasting(
                            connection,
                            userId,
                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, map))
                    );
                })
                .then();
    }

    private Mono<ChannelDetailsInfo> getChannelDetailsInfo(String channelId) {
        log.info("[getChannelDetailsInfo] channelId : {}", channelId);

        return channelService.findChannelDetailsInfoByChannelId(channelId);
    }

    private Mono<List<PlayerInfo>> getPlayerInfoList(String channelId, Long userId) {
        log.info("[getPlayerInfoList] channelId : {}, userId : {}", channelId, userId);

        return channelService.findPlayerListByChannelId(channelId)
                .flatMap(player -> {
                    log.info("[getChannelDetails] player : {}", player);

                    if (!player.getUserId().equals(userId)) {
                        return channelRepository.existByUserId(channelId, player.getUserId())
                                .map(exists -> PlayerInfo.of(player.getNickname(), exists));
                    } else {
                        return Mono.empty();
                    }
                })
                .collectList();
    }

}
