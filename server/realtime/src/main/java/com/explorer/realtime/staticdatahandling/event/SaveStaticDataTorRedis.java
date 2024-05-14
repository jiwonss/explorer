package com.explorer.realtime.staticdatahandling.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveStaticDataTorRedis {

    public Mono<Void> process() {
        return Mono.empty();
    }

}
