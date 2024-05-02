package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestartGame {

//    private final ChannelMongoRepository channelMongoRepository;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ChannelRepository channelRepository;

    public Mono<Void> process(String channel, UserInfo userInfo, Connection connection) {
        // 사용자 정보를 Redis에 저장
        Mono<Void> saveUser = userRepository.save(userInfo);
//                .doOnSuccess(aVoid -> log.info("User info saved successfully."))
//                .doOnError(error -> log.error("Error saving user info: {}", error.getMessage()));

        // 새로운 연결 정보 생성 및 Redis 업데이트
        Mono<Void> saveConnectionInfo = createConnectionInfo(channel, userInfo.getUserId(), connection);

        // 모든 작업 완료 후 Mono<Void> 반환
        return Mono.when(saveUser, saveConnectionInfo)
                .then();
    }

    private Mono<Void> createConnectionInfo(String channel, Long userId, Connection connection) {
        sessionManager.setConnection(String.valueOf(userId), connection);
        return channelRepository.save(channel, userId, 0)
                .then();
//                .doOnSuccess(aVoid -> log.info("Connection info saved successfully."))
//                .doOnError(error -> log.error("Error saving connection info: {}", error.getMessage()));
    }


}

