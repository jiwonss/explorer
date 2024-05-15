package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.event.SetInitialPlayerInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Multicasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomErrorCode;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomException;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestartGame {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ChannelRepository channelRepository;
    private final Unicasting unicasting;
    private final Multicasting multicasting;
    private final LabDataMongoToRedis labDataMongoToRedis;
    private final SetInitialPlayerInfo setInitialPlayerInfo;
    private final InventoryDataMongoToRedis inventoryDataMongoToRedis;

    public Mono<Void> process(String channelId, UserInfo userInfo, Connection connection) {
        // 사용자 정보를 Redis에 저장
        log.info("restart initial");
//        (existChannel(channelId))
        labDataMongoToRedis.process(channelId, "element").subscribe();
        labDataMongoToRedis.process(channelId, "compound").subscribe();
        inventoryDataMongoToRedis.process(channelId, userInfo.getUserId()).subscribe();
        createConnectionInfo(channelId, userInfo, connection).subscribe();
        userRepository.save(userInfo, channelId, "1").subscribe();
//        setInitialPlayerInfo.process(channelId, 8).subscribe();
        return Mono.empty();
    }

    private Mono<Void> createConnectionInfo(String channelId, UserInfo userInfo, Connection connection) {
        sessionManager.setConnection(userInfo.getUserId(), connection);
        setInitialPlayerInfo.process(channelId, 8).subscribe();
        return check(channelId)
                .flatMap(count ->  channelRepository.save(channelId, userInfo.getUserId(), 0)
                    .then((Mono.defer(() -> multicasting.multicasting(channelId, String.valueOf(userInfo.getUserId()), MessageConverter.convert(Message.success("restartGame", CastingType.MULTICASTING, userInfo))))))
                        .then(findAllUserInfoByChannelId(channelId, userInfo.getUserId()))
                    .flatMap(userInfoList ->{
                        return unicasting.unicasting(channelId, userInfo.getUserId(), MessageConverter.convert(Message.success("restartGame", CastingType.UNICASTING, userInfoList)));
                        })
   );
    }

    private Mono<Long> check(String channelId) {
        return channelRepository.count(channelId)
                .flatMap(count -> {
                    log.info("count {}", count);
                    return Mono.just(count);
                });
    }

    private Mono<List<UserInfo>> findAllUserInfoByChannelId(String channelId, Long userId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(id -> {
                    if (!Long.valueOf(String.valueOf(id)).equals(userId)) {
                        return userRepository.findAll(Long.valueOf(String.valueOf(id)))
                                .map(userInfo -> UserInfo.of(
                                        Long.valueOf(String.valueOf(id)),
                                        (String) userInfo.get("nickname"),
                                        Integer.parseInt(String.valueOf(userInfo.get("avatar")))
                                ));
                    } else {
                        return Mono.empty();
                    }
                }).collectList();
    }
}

