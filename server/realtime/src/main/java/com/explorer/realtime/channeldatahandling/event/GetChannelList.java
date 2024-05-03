package com.explorer.realtime.channeldatahandling.event;

import com.explorer.realtime.channeldatahandling.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetChannelList {

    private final UserClient userClient;

    private static final String TOKEN_PREFIX = "Bearer ";

    public Mono<Void> process(String accessToken) {
        log.info("getChannelList - accessToken : {}", accessToken);

        return Mono.empty();
    }

}
