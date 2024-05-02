package com.explorer.chat.servermanaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
@RequiredArgsConstructor
public class ServerInitializer {

    @Value("${chat.server.port}")
    private int PORT;

    private final RequestHandler requestHandler;

    public Mono<? extends DisposableServer> initializeServer() {

        return TcpServer
                .create()
                .port(PORT)
                .handle(requestHandler::handleRequest)
                .bind();
    }
}
