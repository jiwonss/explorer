package com.explorer.logic.servermanaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerInitializer {

    private final RequestHandler requestHandler;

    private static final int PORT = 1379;

    public Mono<? extends DisposableServer> initializeServer() {
            return HttpServer
                    .create()
                    .port(PORT)
                    .handle(requestHandler::handleRequest)
                    .bind();
    }
}
