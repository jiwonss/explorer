package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.repository.ChannelRepository;
import com.explorer.realtime.sessionhandling.repository.UserRepository;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.netty.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveWaitingRoom {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public void process(String teamCode, UserInfo userInfo, Connection connection) {
        if (userInfo.isLeader()) {
            delete(teamCode);
        } else {
            leave(teamCode, userInfo.getUserId());
        }
    }

    private void delete(String teamCode) {
        channelRepository.find(teamCode)
                        .forEach(userId -> userRepository.delete(Long.valueOf(userId)));
        channelRepository.delete(teamCode);
    }

    private void leave(String teamCode, Long userId) {
        channelRepository.leave(teamCode, userId);
        userRepository.delete(userId);
    }

}
