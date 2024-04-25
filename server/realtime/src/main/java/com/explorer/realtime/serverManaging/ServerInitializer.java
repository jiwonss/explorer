package com.explorer.realtime.serverManaging;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
public class ServerInitializer {

    private static final int PORT = 1370;

    public Mono<? extends DisposableServer> initializeServer() {
        return TcpServer
                .create()       // create TCPServer instance
                .port(PORT)     // set port
                .bind();        // bind and start server
    }
}
