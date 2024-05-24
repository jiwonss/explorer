package com.explorer.move.servermanaging;

import com.explorer.move.servermanaging.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;
import reactor.netty.udp.UdpServer;

@Component
@RequiredArgsConstructor
public class ServerInitializer {

    private static final int PORT = 1372;

    private final ConnectionHandler connectionHandler;
    private final RequestHandler requestHandler;

    public Mono<? extends Connection> initializeServer() {
        return UdpServer
                .create()                               // create UDPServer instance
                .port(PORT)                             // set port
                .doOnBound(connectionHandler)
                .handle(requestHandler::handleRequest)  // set up a handler :: handling requests for network connection
                .bind();                                // bind and start server
    }
}
