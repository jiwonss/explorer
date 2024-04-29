package com.explorer.chat.servermanaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
public class ServerInitializer {

    @Value("${chat.server.port}")
    private static int PORT;

    public Mono<? extends DisposableServer> initializeServer() {

        return TcpServer
                .create()
                .port(PORT)
                .bind();
    }
}
