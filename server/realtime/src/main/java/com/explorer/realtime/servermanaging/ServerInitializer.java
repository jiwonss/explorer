package com.explorer.realtime.servermanaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
@RequiredArgsConstructor
public class ServerInitializer {

    private static final int PORT = 1370;

    private final ConnectionHandler connectionHandler;
    private final RequestHandler requestHandler;

    public Mono<? extends DisposableServer> initializeServer() {
        return TcpServer
                .create()                               // create TCPServer instance
                .port(PORT)                             // set port
                .doOnConnection(connectionHandler)
                .handle(requestHandler::handleRequest)  // set up a handler :: handling requests for network connection
                .bind();                                // bind and start server
    }
}
