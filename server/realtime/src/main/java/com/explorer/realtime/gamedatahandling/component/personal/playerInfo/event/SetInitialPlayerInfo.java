package com.explorer.realtime.gamedatahandling.component.personal.playerInfo.event;

import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetInitialPlayerInfo {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final PlayerInfoRepository playerInfoRepository;

    public Mono<Void> process(String channelId, int inventoryCnt) {
        log.info("[precess] channelId : {}", channelId);

        return saveAllInitPlayerInfoByChannelId(channelId, inventoryCnt);
    }
    private Mono<Void> saveAllInitPlayerInfoByChannelId(String channelId, int inventoryCnt) {
        log.info("[saveAllInitPlayerInfoByChannelId] channelId : {}", channelId);

        return channelRepository.findAllFields(channelId)
                .flatMap(userId -> userRepository.findAll(Long.valueOf(String.valueOf(userId)))
                        .flatMap(map -> {
                            log.info("[saveAllInitPlayerInfoByChannelId] map : {}", map);
                            return playerInfoRepository.init(
                                    channelId,
                                    Long.valueOf(String.valueOf(userId)),
                                    String.valueOf(map.get("nickname")),
                                    Integer.parseInt(String.valueOf(map.get("avatar"))),
                                    Integer.parseInt(String.valueOf(inventoryCnt))
                            );
                        }))
                .then();
    }

}
