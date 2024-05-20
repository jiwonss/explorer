package com.explorer.logic.laboratory.upgrade;

import com.explorer.logic.laboratory.upgrade.event.GetMaterials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabUpgradeHandler {

    private final GetMaterials getMaterials;

    public Mono<Void> labUpgradeHandler(HttpServerRequest request, HttpServerResponse response) {
        log.info("Received POST request on lab/upgrade");

        return request
                .receive()
                .asString()
                .flatMap(body -> response.status(200).sendString(getMaterials.process(body)))
                .then();
    }
}
