package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.repository.ConnectionRepository;
import com.explorer.realtime.sessionhandling.repository.UserInfoRepository;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.error.ExceedingCapacityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.netty.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinWaitingRoom {

    private final ConnectionRepository connectionRepository;
    private final UserInfoRepository userInfoRepository;

    public void process(String teamCode, UserInfo userInfo, Connection connection) {
        try {
            check(teamCode);
            connectionRepository.save(teamCode,userInfo.getUserId(), connection);
            userInfoRepository.save(userInfo);
        } catch (ExceedingCapacityException ex) {
            ex.printStackTrace();
        }
    }

    public void check(String teamCode) {
        if (connectionRepository.count(teamCode) == 6) {
            throw  new ExceedingCapacityException();
        }
    }

}
