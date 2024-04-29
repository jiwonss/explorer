package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.global.teamcode.TeamCodeGenerator;
import com.explorer.realtime.sessionhandling.repository.ChannelRepository;
import com.explorer.realtime.sessionhandling.repository.UserRepository;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.netty.Connection;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateWaitingRoom {

    private final TeamCodeGenerator teamCodeGenerator;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public void process(UserInfo userInfo, Connection connection) {
        String teamCode = createTeamCode();
        channelRepository.save(teamCode, userInfo.getUserId(), connection);
        userRepository.save(userInfo);
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
