package com.explorer.realtime.global.component.broadcasting;

import com.explorer.realtime.global.redis.RedisService;
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
    private final RedisService redisService;
    private final SessionManager sessionManager;

    public Multicasting(RedisService redisService, SessionManager sessionManager) {
        this.redisService = redisService;
        this.sessionManager = sessionManager;
    }

    public Mono<Void> multicasting(String teamCode, String uid, JSONObject msg) {

        log.info("start multicasting to {}", teamCode);

        return redisService.readUidsFromTeamCode(teamCode)
                .flatMapMany(hashTable -> {
                    return Flux.fromIterable(hashTable.keySet())
                            .flatMap(key -> {
                                if (key.equals(uid)) {
                                    return Mono.empty();
                                }

                                Connection connection = sessionManager.getConnection(key.toString());

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
