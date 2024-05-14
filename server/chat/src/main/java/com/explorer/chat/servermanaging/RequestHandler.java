package com.explorer.chat.servermanaging;

import com.explorer.chat.chatdatahandling.ChatDataHandler;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

@Component
@RequiredArgsConstructor
public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final ChatDataHandler chatDataHandler;

    public Mono<Void> handleRequest(NettyInbound inbound, NettyOutbound outbound) {

        return inbound
                .receive()
                .asString()
                .flatMap(msg -> {
                    try {
                        JSONObject json = new JSONObject(msg);
                        log.info("Received message: {}", msg);

                        inbound.withConnection(connection -> {
                            String type = json.getString("type");

                            switch (type) {
                                case "session":
                                    break;

                                case "chat":
                                    log.info("type : {}", type);
                                    chatDataHandler.chatDataHandler(json, connection);
                                    break;
                            }
                        });

                        return Mono.empty();
                    } catch (JSONException e) {
                        log.error("ERROR: {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .then();
    }
}
