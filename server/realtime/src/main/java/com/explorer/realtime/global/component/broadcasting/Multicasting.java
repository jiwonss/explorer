package com.explorer.realtime.global.component.broadcasting;

import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.component.session.SessionManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
public class Multicasting {

    private static final Logger log = LoggerFactory.getLogger(Multicasting.class);
    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;

    public Multicasting(ChannelRepository channelRepository, SessionManager sessionManager) {
        this.channelRepository = channelRepository;
        this.sessionManager = sessionManager;
    }

    public Mono<Void> multicasting(String teamCode, String uid, JSONObject msg) {

        log.info("start multicasting to {}", teamCode);

        return channelRepository.findAll(teamCode)
                .flatMapMany(hashTable -> {
                    return Flux.fromIterable(hashTable.keySet())
                            .flatMap(key -> {
                                if (key.equals(uid)) {
                                    return Mono.empty();
                                }

                                Connection connection = sessionManager.getConnection(Long.valueOf(key.toString()));

                                if (connection != null) {
                                    log.info("sending message to {}, msg: {}", key, msg);
                                    return connection.outbound().sendString(Mono.just(msg.toString() + '\n')).then();
                                } else {
                                    log.warn("No connection found for {}", key);
                                    return Mono.empty();
                                }
                            });
                })
                .then()
                .doOnSuccess(aVoid -> log.info("Multicast completed for teamCode: {}", teamCode))
                .doOnError(error -> log.error("Multicast failed for teamCode: {}, error: {}", teamCode, error.getMessage()));
    }
}
