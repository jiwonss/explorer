package com.explorer.chat.global.component.broadcasting;

import com.explorer.chat.global.component.session.SessionManager;
import com.explorer.chat.global.redis.ChannelRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
public class Broadcasting {

    private static final Logger log = LoggerFactory.getLogger(Broadcasting.class);
    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;

    public Broadcasting(ChannelRepository channelRepository, SessionManager sessionManager) {
        this.channelRepository = channelRepository;
        this.sessionManager = sessionManager;
    }
    public Mono<Void> broadcasting(String teamCode, JSONObject msg) {
        log.info("start broadcasting to {}", teamCode);
        return channelRepository.findAll(teamCode)
                .flatMapMany(hashTable -> {
                    return Flux.fromIterable(hashTable.keySet())
                            .flatMap(key -> {
                                Connection connection = sessionManager.getConnection(Long.valueOf(key.toString()));
                                if (connection != null) {
                                    log.info("sending message to {}", key);
                                    return connection.outbound().sendString(Mono.just(msg.toString()+'\n')).then();
                                } else {
                                    log.warn("No connection found for {}", key);
                                    return Mono.empty();
                                }
                            });
                })
                .then()
                .doOnSuccess(aVoid -> log.info("Broadcast completed for teamCode: {}", teamCode))
                .doOnError(error -> log.error("Broadcast failed tor teamCode: {}, error: {}", teamCode, error.getMessage()));
    }
}
