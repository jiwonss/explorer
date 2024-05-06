package com.explorer.logic.servermanaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@Service
public class ServerInitializer {

    @Value("${server.port:1373}")
    private static int PORT;

    public Mono<? extends DisposableServer> initializeServer() {
            return HttpServer
                    .create()
                    .port(PORT)
                    .bind();
    }
}
