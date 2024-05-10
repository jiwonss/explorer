package com.explorer.realtime.gamedatahandling.tool;

import com.explorer.realtime.gamedatahandling.tool.event.AttachTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolHandler {

    private final AttachTool attachTool;

    public Mono<Void> toolHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "attachTool":
                log.info("eventName : {}", eventName);
                attachTool.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }


}
