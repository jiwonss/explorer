package com.explorer.realtime.global.component.broadcasting;

import com.explorer.realtime.global.component.session.SessionManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
public class Unicasting {

    private static final Logger log = LoggerFactory.getLogger(Unicasting.class);
    private final SessionManager sessionManager;

    public Unicasting(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Mono<Void> unicasting(String teamCode, Long userId, JSONObject msg) {

        log.info("start unicasting to {} from {}", userId, teamCode);

        Connection connection = sessionManager.getConnection(userId);

        if (connection == null) {
            log.warn("No connection found for {}", userId);
            return Mono.empty();
        }

        return connection.outbound().sendString(Mono.just(msg.toString()+'\n'))
                .then()
                .doOnSuccess(aVoid -> log.info("Unicast completed for teamCode: {}", teamCode))
                .doOnError(error -> log.error("Unicast failed for teamCode: {}, error: {}", teamCode, error.getMessage()));
    }

    public Mono<Void> unicasting(Connection connection, Long userId, JSONObject msg) {
        log.info("start unicasting to {}", userId);

        if (connection == null) {
            log.warn("No connection found for {}", userId);
            return Mono.empty();
        }

        return connection.outbound().sendString(Mono.just(msg.toString()+'\n'))
                .then()
                .doOnSuccess(aVoid -> log.info("Unicast completed for userId : {}", userId))
                .doOnError(error -> log.error("Unicast failed for userId: {}, error: {}", userId, error.getMessage()));
    }

}
