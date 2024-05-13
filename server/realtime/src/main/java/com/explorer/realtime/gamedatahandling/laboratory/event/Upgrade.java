package com.explorer.realtime.gamedatahandling.laboratory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Upgrade {

    @Value("${logic.laboratory.upgrade-url}")
    private String upgradeUrl;

    public Mono<Void> process(JSONObject json) {

        return Mono.empty();
    }

    private Mono<Void> checkLevel(JSONObject json) {
        return Mono.empty();
    }

}
