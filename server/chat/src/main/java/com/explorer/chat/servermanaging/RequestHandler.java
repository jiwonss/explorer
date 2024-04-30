package com.explorer.chat.servermanaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

@Component
public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Mono<Void> handleRequest(NettyInbound inbound, NettyOutbound outbound) {

        return inbound
                .receive()
                .asString()
                .flatMap(msg -> {

                    log.info("Received message: {}", msg);
                    return outbound.sendString(Mono.just(msg));

                })
                .then();
    }
}
