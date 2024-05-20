package com.explorer.logic.laboratory.extract;

import com.explorer.logic.laboratory.extract.event.Calculate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabExtractHandler {

    private final Calculate calculate;

    public Mono<Void> labExtractHandler(HttpServerRequest request, HttpServerResponse response) {
        log.info("Received POST request on lab/extract");

        return request
                .receive()
                .asString()
                .flatMap(body -> response.status(200).sendString(calculate.process(body)))
                .then();
    }
}
