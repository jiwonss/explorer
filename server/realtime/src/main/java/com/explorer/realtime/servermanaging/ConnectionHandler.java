package com.explorer.realtime.servermanaging;

import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.sessionhandling.disconnect.event.LeaveGame;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.netty.Connection;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionHandler implements Consumer<Connection> {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final LeaveGame leaveGame;

    @Override
    public void accept(Connection connection) {
        connection.addHandlerFirst(new ChannelHandlerAdapter() {

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                super.handlerAdded(ctx);
                log.info("Client connected: {}", connection.address());
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                super.handlerRemoved(ctx);
                Long userId = sessionManager.getUid(connection);
                userRepository.findAll(userId).subscribe(userInfo -> {
                    String channelId = String.valueOf(userInfo.get("channelId"));
                    String isInGame = String.valueOf(userInfo.get("mapId"));

                    switch (isInGame) {
                        case "0":
                            log.info("[WAITINGROOM] Client leaved >> userId:{}, channelId:{}, isInGame:{}", userId, channelId, isInGame);
                            break;
                        case "1":
                            log.info("[INGAME] Client leaved >> userId:{}, channelId:{}, isInGame:{}", userId, channelId, isInGame);
                            leaveGame.process(channelId, userId).subscribe();
                            break;
                    }

                }, error -> {
                    log.error("Failed to fetch user info for userId {}: {}", userId, error.getMessage());
                });

            }
        });
    }
}