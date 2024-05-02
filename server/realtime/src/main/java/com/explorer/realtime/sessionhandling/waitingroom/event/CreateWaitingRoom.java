package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.component.teamcode.TeamCodeGenerator;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.TempRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWaitingRoom {

    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;
    private final TeamCodeGenerator teamCodeGenerator;
    private final UserRepository userRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(UserInfo userInfo, Connection connection) {
        String teamCode = createTeamCode();
        createConnectionInfo(teamCode, userInfo.getUserId(), connection);
        userRepository.save(userInfo).subscribe();

        Map<String, String> map = new HashMap<>();
        map.put("teamCode", teamCode);

        unicasting.unicasting(
                teamCode,
                String.valueOf(userInfo.getUserId()),
                MessageConverter.convert(Message.success("createWaitingRoom", CastingType.UNICASTING, map))
        ).subscribe();

        return Mono.empty();
    }

    private void createConnectionInfo(String teamCode, Long userId, Connection connection) {
        sessionManager.setConnection(String.valueOf(userId), connection);
        channelRepository.save(teamCode, userId, 0).subscribe();
    }

    private String createTeamCode() {
        AtomicReference<String> teamCode = new AtomicReference<>();
        teamCodeGenerator.getCode().subscribe(
                code -> {
                    teamCode.set(code);
                    log.info("teamCode : {}", teamCode);
                },
                error -> {
                    log.info("error fetching team code");
                }
        );
        return teamCode.toString();
    }

}
