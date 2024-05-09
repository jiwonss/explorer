package com.explorer.logic.farm;

import com.explorer.logic.farm.event.Calculate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmHandler {

    private final Calculate calculate;

    public Mono<Void> farmHandler(HttpServerRequest request, HttpServerResponse response) {
        log.info("Received POST request on lab/extract");

        return request
                .receive()
                .asString()
                .flatMap(body -> response.status(200).sendString(calculate.process(body)))
                .then();

    }
}
