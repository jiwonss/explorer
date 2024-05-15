package com.explorer.realtime.gamedatahandling.laboratory;

import com.explorer.realtime.gamedatahandling.laboratory.event.EnterLab;
import com.explorer.realtime.gamedatahandling.laboratory.event.Extract;
import com.explorer.realtime.gamedatahandling.laboratory.event.Synthesize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaboratoryHandler {

    private final Extract extract;
    private final Synthesize synthesize;
    private final EnterLab enterLab;

    public Mono<Void> laboratoryHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "extracting":
                log.info("eventName : {}", eventName);
                return extract.process(json);
            case "synthesizing":
                log.info("eventName : {}", eventName);
                return synthesize.process(json);
            case "enterLab":
                log.info("eventName : {}", eventName);
                return enterLab.process(json);
        }
        return Mono.empty();
    }
}
