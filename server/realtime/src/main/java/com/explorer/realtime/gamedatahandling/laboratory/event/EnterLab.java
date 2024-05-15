package com.explorer.realtime.gamedatahandling.laboratory.event;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EnterLab {

    public Mono<Void> process(JSONObject json) {

        log.info("EnterLab process start...");

        return Mono.empty();
    }
}
