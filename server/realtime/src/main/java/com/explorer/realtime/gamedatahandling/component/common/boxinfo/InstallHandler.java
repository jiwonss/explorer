package com.explorer.realtime.gamedatahandling.component.common.boxinfo;

import com.explorer.realtime.gamedatahandling.component.common.boxinfo.event.BoxInstall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstallHandler {

    private final BoxInstall boxInstall;

    public Mono<Void> boxHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "installBox":

                log.info("eventName : {}", eventName);
                boxInstall.process(json).subscribe();
                break;
        }
        return Mono.empty();
    }
}
