package com.explorer.move.servermanaging;

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

    @Override
    public void accept(Connection connection) {
        connection.addHandlerFirst(new ChannelHandlerAdapter() {

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                super.handlerAdded(ctx);
                log.info("Client connected: {}", connection.address());
            }
        });
    }
}