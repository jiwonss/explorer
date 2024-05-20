package com.explorer.logic.laboratory.synthesize;

import com.explorer.logic.laboratory.synthesize.event.GetElements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabSynthesizeHandler {

    private final GetElements getElements;

    public Mono<Void> labSynthesizeHandler(HttpServerRequest request, HttpServerResponse response) {
        log.info("Received POST request on lab/synthesize");

        return request
                .receive()
                .asString()
                .flatMap(body -> response.status(200).sendString(getElements.process(body)))
                .then();
    }
}