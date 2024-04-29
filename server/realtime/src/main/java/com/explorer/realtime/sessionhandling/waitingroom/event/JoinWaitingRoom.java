package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.sessionhandling.waitingroom.repository.ChannelRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.ExceedingCapacityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.netty.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinWaitingRoom {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public void process(String teamCode, UserInfo userInfo, Connection connection) {
        try {
            check(teamCode);
            channelRepository.save(teamCode,userInfo.getUserId(), connection);
            userRepository.save(userInfo);
        } catch (ExceedingCapacityException ex) {
            ex.printStackTrace();
        }
    }

    private void check(String teamCode) {
        if (channelRepository.count(teamCode) == 6) {
            throw  new ExceedingCapacityException();
        }
    }

}
