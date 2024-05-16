package com.explorer.realtime.gamedatahandling.laboratory;

import com.explorer.realtime.gamedatahandling.laboratory.event.*;
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
    private final LeaveLab leaveLab;
    private final Upgrade upgrade;

    public Mono<Void> laboratoryHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        return switch (eventName) {
            case "extracting" -> {
                log.info("eventName : {}", eventName);
                yield extract.process(json);
            }
            case "synthesizing" -> {
                log.info("eventName : {}", eventName);
                yield synthesize.process(json);
            }
            case "enterLab" -> {
                log.info("eventName : {}", eventName);
                yield enterLab.process(json);
            }
            case "leaveLab" -> {
                log.info("eventName : {}", eventName);
                yield leaveLab.process(json);
            }
            case "upgrade" -> {
                log.info("eventName : {}", eventName);
                yield upgrade.process(json);
            }
            default -> Mono.empty();
        };
    }
}
