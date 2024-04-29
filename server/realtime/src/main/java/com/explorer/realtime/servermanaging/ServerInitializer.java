package com.explorer.realtime.servermanaging;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Component
public class ServerInitializer {

    private static final int PORT = 1370;

    private final RequestHandler requestHandler;

    public ServerInitializer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public Mono<? extends DisposableServer> initializeServer() {
        return TcpServer
                .create()                               // create TCPServer instance
                .port(PORT)                             // set port
                .handle(requestHandler::handleRequest)  // set up a handler :: handling requests for network connection
                .bind();                                // bind and start server
    }
}
