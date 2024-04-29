package com.explorer.realtime.global.broadcasting;

import com.explorer.realtime.global.session.SessionManager;
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

    public Mono<Void> unicasting(String teamCode, String uid, JSONObject msg) {

        log.info("start unicasting to {} from {}", uid, teamCode);

        Connection connection = sessionManager.getConnection(uid);

        if (connection == null) {
            log.warn("No connection found for {}", uid);
            return Mono.empty();
        }

        return connection.outbound().sendString(Mono.just(msg.toString()+'\n'))
                .then()
                .doOnSuccess(aVoid -> log.info("Unicast completed for teamCode: {}", teamCode))
                .doOnError(error -> log.error("Unicast failed for teamCode: {}, error: {}", teamCode, error.getMessage()));
    }

}
